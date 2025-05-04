package cn.alphacat.chinastocktrader.service.marketindex;

import cn.alphacat.chinastockdata.enums.KLineTypeEnum;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;

import cn.alphacat.chinastocktrader.model.OnePercentVolatilityFunds;
import cn.alphacat.chinastocktrader.repository.MarketIndexRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class SSEIndexHistoryService {
  private final MarketService marketService;
  private final MarketIndexRepository marketIndexRepository;

  private final Executor taskExecutor;

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
    Optional<LocalDate> earliestTradeDateInDB =
        marketIndexRepository.findEarliestTradeDateByIndexCode(SSE_INDEX_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      List<MarketIndex> marketIndexes = getAdataMarketIndices(startDate);
      List<MarketIndexEntity> entities =
          marketIndexes.stream()
              .filter(MarketIndex::checkValid)
              .map(EntityConverter::convertToEntity)
              .toList();
      marketIndexRepository.saveAll(entities);
      return marketIndexes;
    }
    LocalDate earliestTradeDateValueInDB = earliestTradeDateInDB.get();
    LocalDate latestTradeDateValueInDB =
        marketIndexRepository
            .findLatestTradeDateByIndexCode(SSE_INDEX_CODE)
            .orElse(earliestTradeDateValueInDB);

    CompletableFuture.runAsync(
        () ->
            getDataFromAPIAndSaveToDB(
                startDate, earliestTradeDateValueInDB, latestTradeDateValueInDB),
        taskExecutor).exceptionally(ex -> {
      log.error("Failed from API getShanghaiIndexHistory: {}", ex.getMessage());
      return null;
    });

    List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
        marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate, SSE_INDEX_CODE);
    return allByTradeDateGreaterThanOrEqualTo.stream()
        .map(EntityConverter::convertToModel)
        .toList();
  }

  private void getDataFromAPIAndSaveToDB(
      LocalDate startDate,
      LocalDate earliestTradeDateValueInDB,
      LocalDate latestTradeDateValueInDB) {
    List<MarketIndex> marketIndexs = getAdataMarketIndices(startDate);
    List<MarketIndexEntity> entitiesToSave =
        marketIndexs.stream()
            .filter(
                index -> {
                  if (!index.checkValid()) {
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
  }

  private List<MarketIndex> getAdataMarketIndices(LocalDate startDate) {
    List<MarketIndex> marketIndexs =
        marketService.getMarketIndex(SSE_INDEX_CODE, startDate, KLineTypeEnum.DAILY);
    if (TimeUtil.isAfterStockCloseTime()) {
      return marketIndexs;
    }
    return marketIndexs.stream()
        .filter(index -> !index.getTradeDate().isEqual(LocalDate.now()))
        .toList();
  }
}
