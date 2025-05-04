package cn.alphacat.chinastocktrader.service.marketindex;

import cn.alphacat.chinastockdata.enums.KLineTypeEnum;
import cn.alphacat.chinastockdata.market.LeguLeguService;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.marketindex.IndexPE;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.entity.IndexPEEntity;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;
import cn.alphacat.chinastocktrader.repository.IndexPERepository;
import cn.alphacat.chinastocktrader.repository.MarketIndexRepository;
import cn.alphacat.chinastocktrader.service.market.TradeCalendarService;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import cn.alphacat.chinastockdata.enums.LuguLuguIndexPEEnums;

@Service
@Slf4j
public class CSI300IndexService {
  private final MarketService marketService;
  private final LeguLeguService leguLeguService;

  private final Executor taskExecutor;

  private final MarketIndexRepository marketIndexRepository;
  private final IndexPERepository indexPERepository;

  private final Map<String, Boolean> requestCache = new ConcurrentHashMap<>();

  private static final String CSI300_CODE = "000300";

  public CSI300IndexService(
      final MarketService marketService,
      final MarketIndexRepository marketIndexRepository,
      final LeguLeguService leguLeguService,
      final Executor taskExecutor,
      final IndexPERepository indexPERepository) {
    this.marketService = marketService;
    this.marketIndexRepository = marketIndexRepository;
    this.leguLeguService = leguLeguService;
    this.taskExecutor = taskExecutor;
    this.indexPERepository = indexPERepository;
  }

  public List<MarketIndex> getCSI300IndexDaily(LocalDate startDate) {
    Optional<LocalDate> earliestTradeDateInDB =
        marketIndexRepository.findEarliestTradeDateByIndexCode(CSI300_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      List<MarketIndex> marketIndexes = getCSI300IndexDailyFromAPI(startDate);
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
            .findLatestTradeDateByIndexCode(CSI300_CODE)
            .orElse(earliestTradeDateValueInDB);

    CompletableFuture.runAsync(
            () ->
                getDataFromAPIAndSaveToDB(
                    startDate, earliestTradeDateValueInDB, latestTradeDateValueInDB),
            taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getCSI300IndexDaily: {}", ex.getMessage());
              return null;
            });

    List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
        marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate, CSI300_CODE);

    return allByTradeDateGreaterThanOrEqualTo.stream()
        .map(EntityConverter::convertToModel)
        .toList();
  }

  private void getDataFromAPIAndSaveToDB(
      LocalDate startDate,
      LocalDate earliestTradeDateValueInDB,
      LocalDate latestTradeDateValueInDB) {
    List<MarketIndex> marketIndexs = getCSI300IndexDailyFromAPI(startDate);
    List<MarketIndexEntity> entitiesToSave =
        marketIndexs.stream()
            .filter(
                index -> {
                  if (!index.checkValid()) {
                    return false;
                  }
                  if (index.getTradeDate().isEqual(LocalDateUtil.getNow())) {
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

  public Map<LocalDate, IndexPE> getCSI300IndexPE(LocalDate startDate) {
    CompletableFuture.runAsync(this::getCSI300IndexPEFromAPIAndSaveToDB, taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getCSI300IndexPE: {}", ex.getMessage());
              return null;
            });
    List<IndexPEEntity> entities =
        indexPERepository.findByIndexCodeAndDateIsGreaterThanEqual(
            LuguLuguIndexPEEnums.SCI300.getIndeCode(), startDate);
    return entities.stream()
        .collect(
            java.util.stream.Collectors.toMap(
                IndexPEEntity::getDate, EntityConverter::convertToModel));
  }

  private void getCSI300IndexPEFromAPIAndSaveToDB() {
    Optional<LocalDate> top1DateByIndexCodeOrderByDateDesc =
        indexPERepository.findTop1DateByIndexCodeOrderByDateDesc(
            LuguLuguIndexPEEnums.SCI300.getIndeCode());
    if (top1DateByIndexCodeOrderByDateDesc.isEmpty()) {
      Map<LocalDate, IndexPE> stockIndexPE = getStockIndexPE();
      List<IndexPEEntity> entities =
          stockIndexPE.values().stream()
              .filter(
                  entity -> {
                    LocalDate date = entity.getDate();
                    if (date == null) {
                      return false;
                    }
                    return !date.isEqual(LocalDateUtil.getNow());
                  })
              .map(EntityConverter::convertToEntity)
              .toList();

      indexPERepository.saveAll(entities);
      return;
    }
    LocalDate latestDateInDB = top1DateByIndexCodeOrderByDateDesc.get();

    Map<LocalDate, IndexPE> stockIndexPE = getStockIndexPE();
    List<IndexPEEntity> entities =
        stockIndexPE.entrySet().stream()
            .filter(
                entry -> {
                  LocalDate localDate = entry.getKey();
                  if (localDate.isEqual(LocalDateUtil.getNow())) {
                    return false;
                  }
                  return localDate.isAfter(latestDateInDB);
                })
            .map(item -> EntityConverter.convertToEntity(item.getValue()))
            .toList();
    indexPERepository.saveAll(entities);
  }

  private List<MarketIndex> getCSI300IndexDailyFromAPI(LocalDate startDate) {
    List<MarketIndex> marketIndex =
        marketService.getMarketIndex(CSI300_CODE, startDate, KLineTypeEnum.DAILY);
    return marketIndex.stream()
        .filter(
            index -> {
              LocalDate tradeDate = index.getTradeDate();
              if (tradeDate == null) {
                return false;
              }
              return !tradeDate.isEqual(LocalDateUtil.getNow());
            })
        .toList();
  }

  private Map<LocalDate, IndexPE> getStockIndexPE() {
    String todayKey = LocalDateUtil.getNow().toString();
    if (Boolean.TRUE.equals(requestCache.get(todayKey))) {
      return Collections.emptyMap();
    }
    synchronized (this) {
      if (Boolean.TRUE.equals(requestCache.get(todayKey))) {
        return Collections.emptyMap();
      }
      try {
        Map<LocalDate, IndexPE> result =
            leguLeguService.getStockIndexPE(LuguLuguIndexPEEnums.SCI300);
        requestCache.put(todayKey, true);
        return result;
      } catch (Exception e) {
        requestCache.remove(todayKey);
        log.error("Failed to getStockIndexPE: {}", e.getMessage());
        return Collections.emptyMap();
      }
    }
  }
}
