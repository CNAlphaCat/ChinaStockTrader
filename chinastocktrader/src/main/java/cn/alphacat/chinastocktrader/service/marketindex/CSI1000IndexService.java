package cn.alphacat.chinastocktrader.service.marketindex;

import cn.alphacat.chinastockdata.enums.KLineTypeEnum;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;
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
public class CSI1000IndexService {
  private final MarketService marketService;
  private final MarketIndexRepository marketIndexRepository;

  private final Executor taskExecutor;

  private static final String CSI1000_CODE = "000852";

  private final ReentrantLock lock = new ReentrantLock();

  public CSI1000IndexService(
      final MarketService marketService,
      final MarketIndexRepository marketIndexRepository,
      final Executor taskExecutor) {
    this.marketService = marketService;
    this.marketIndexRepository = marketIndexRepository;
    this.taskExecutor = taskExecutor;
  }

  public List<MarketIndex> getCSI1000IndexDaily(LocalDate startDate) {
    if (marketIndexRepository.count() == 0) {
      return initData(startDate);
    }
    CompletableFuture.runAsync(() -> getDataFromAPIAndSaveToDB(startDate), taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getSortedStockLimitView: {}", ex.getMessage());
              return null;
            });

    List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
        marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate, CSI1000_CODE);
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
            .findAllByTradeDateGreaterThanOrEqualTo(startDate, CSI1000_CODE)
            .stream()
            .sorted(Comparator.comparing(MarketIndexEntity::getTradeDate))
            .map(EntityConverter::convertToModel)
            .toList();
      }
      List<MarketIndex> marketIndexes = getCSI1000IndexDailyFromAPI(startDate);
      List<MarketIndexEntity> entities =
          marketIndexes.stream()
              .filter(MarketIndex::checkValid)
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
        marketIndexRepository.findEarliestTradeDateByIndexCode(CSI1000_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      initData(startDate);
      return;
    }
    LocalDate earliestTradeDateValueInDB = earliestTradeDateInDB.get();
    lock.lock();
    try {
      LocalDate latestTradeDateValueInDB =
          marketIndexRepository
              .findLatestTradeDateByIndexCode(CSI1000_CODE)
              .orElse(earliestTradeDateValueInDB);
      List<MarketIndex> marketIndexs = getCSI1000IndexDailyFromAPI(startDate);
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

  private List<MarketIndex> getCSI1000IndexDailyFromAPI(LocalDate startDate) {
    List<MarketIndex> marketIndex =
        marketService.getMarketIndex(CSI1000_CODE, startDate, KLineTypeEnum.DAILY);
    return marketIndex.stream()
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
