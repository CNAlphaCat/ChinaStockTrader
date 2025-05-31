package cn.alphacat.chinastocktrader.service.marketindex;

import cn.alphacat.chinastockdata.enums.KLineTypeEnum;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;

import cn.alphacat.chinastocktrader.model.OnePercentVolatilityFunds;
import cn.alphacat.chinastocktrader.repository.MarketIndexRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateTimeUtil;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class SSEIndexHistoryService {
  private final MarketService marketService;
  private final MarketIndexRepository marketIndexRepository;

  private final Executor taskExecutor;

  private final ReentrantLock lock = new ReentrantLock();

  private static final String SSE_INDEX_CODE = "000001";

  public SSEIndexHistoryService(
      final MarketService marketService,
      final MarketIndexRepository marketIndexRepository,
      final Executor taskExecutor) {
    this.marketService = marketService;
    this.marketIndexRepository = marketIndexRepository;
    this.taskExecutor = taskExecutor;
  }

  public List<OnePercentVolatilityFunds> getSortedOnePercentVolatilityFunds(LocalDate startDate) {
    List<MarketIndex> shanghaiIndexHistory = getShanghaiIndexHistory(startDate);
    return shanghaiIndexHistory.stream()
        .sorted(Comparator.comparing(MarketIndex::getTradeDate))
        .map(OnePercentVolatilityFunds::new)
        .toList();
  }

  public List<MarketIndex> getShanghaiIndexHistory(LocalDate startDate) {
    if (marketIndexRepository.count() == 0) {
      return initData(startDate);
    }
    CompletableFuture.runAsync(() -> getDataFromAPIAndSaveToDB(startDate), taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getShanghaiIndexHistory: {}", ex.getMessage());
              return null;
            });

    List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
        marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate, SSE_INDEX_CODE);
    return allByTradeDateGreaterThanOrEqualTo.stream()
        .map(EntityConverter::convertToModel)
        .sorted(Comparator.comparing(MarketIndex::getTradeDate))
        .toList();
  }

  private List<MarketIndex> initData(LocalDate startDate) {
    lock.lock();
    try {
      if (marketIndexRepository.count() > 0) {
        return marketIndexRepository
            .findAllByTradeDateGreaterThanOrEqualTo(startDate, SSE_INDEX_CODE)
            .stream()
            .map(EntityConverter::convertToModel)
            .toList();
      }
      List<MarketIndex> marketIndexes = getMarketIndices(startDate);
      List<MarketIndexEntity> entities =
          marketIndexes.stream()
              .filter(MarketIndex::checkValid)
              .sorted(Comparator.comparing(MarketIndex::getTradeDate))
              .map(EntityConverter::convertToEntity)
              .toList();
      marketIndexRepository.saveAll(entities);
      return marketIndexes;
    } finally {
      lock.unlock();
    }
  }

  private void getDataFromAPIAndSaveToDB(LocalDate startDate) {
    Optional<LocalDate> earliestTradeDateInDB =
        marketIndexRepository.findEarliestTradeDateByIndexCode(SSE_INDEX_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      initData(startDate);
      return;
    }
    lock.lock();
    try {
      LocalDate earliestTradeDateValueInDB = earliestTradeDateInDB.get();
      LocalDate latestTradeDateValueInDB =
          marketIndexRepository
              .findLatestTradeDateByIndexCode(SSE_INDEX_CODE)
              .orElse(earliestTradeDateValueInDB);
      List<MarketIndex> marketIndexs = getMarketIndices(startDate);
      List<MarketIndexEntity> entitiesToSave =
          marketIndexs.stream()
              .filter(
                  index -> {
                    if (!index.checkValid()) {
                      return false;
                    }
                    if (index.getTradeDate().isEqual(LocalDateUtil.getNow())
                        && LocalDateTimeUtil.isBeforeEqualStockCloseTime()) {
                      return false;
                    }
                    if (index.getTradeDate().isBefore(earliestTradeDateValueInDB)) {
                      return true;
                    }
                    return index.getTradeDate().isAfter(latestTradeDateValueInDB);
                  })
              .map(EntityConverter::convertToEntity)
              .toList();
      marketIndexRepository.saveAll(entitiesToSave);
    } finally {
      lock.unlock();
    }
  }

  private List<MarketIndex> getMarketIndices(LocalDate startDate) {
    List<MarketIndex> marketIndexs =
        marketService.getMarketIndex(SSE_INDEX_CODE, startDate, KLineTypeEnum.DAILY);
    return marketIndexs.stream()
        .filter(
            index -> {
              LocalDate tradeDate = index.getTradeDate();
              if (tradeDate == null) {
                return false;
              }
              if (tradeDate.isEqual(LocalDateUtil.getNow())
                  && LocalDateTimeUtil.isBeforeEqualStockCloseTime()) {
                return false;
              }
              return true;
            })
        .toList();
  }
}
