package cn.alphacat.chinastocktrader.service.market;

import cn.alphacat.chinastockdata.enums.TradeStatusEnum;
import cn.alphacat.chinastockdata.market.EastMoneyMarketStockLimitService;
import cn.alphacat.chinastockdata.model.SZSECalendar;
import cn.alphacat.chinastockdata.model.stock.StockLimitDownSummary;
import cn.alphacat.chinastockdata.model.stock.StockLimitUpSummary;
import cn.alphacat.chinastocktrader.entity.StockLimitEntity;
import cn.alphacat.chinastocktrader.repository.StockLimitRepository;
import cn.alphacat.chinastocktrader.util.AlgorithmUtil;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateTimeUtil;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.StockLimitView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class MarketStatisticService {
  private final EastMoneyMarketStockLimitService eastMoneyMarketStockLimitService;
  private final TradeCalendarService tradeCalendarService;
  private final StockLimitRepository stockLimitRepository;

  private final Executor taskExecutor;

  private final ReentrantLock lock = new ReentrantLock();

  public MarketStatisticService(
      final EastMoneyMarketStockLimitService eastMoneyMarketStockLimitService,
      final TradeCalendarService tradeCalendarService,
      final StockLimitRepository stockLimitRepository,
      final Executor taskExecutor) {
    this.eastMoneyMarketStockLimitService = eastMoneyMarketStockLimitService;
    this.tradeCalendarService = tradeCalendarService;
    this.stockLimitRepository = stockLimitRepository;
    this.taskExecutor = taskExecutor;
  }

  public List<StockLimitView> getSortedStockLimitView() {
    Optional<LocalDate> maxTradeDateOpt = stockLimitRepository.findMaxTradeDate();
    if (maxTradeDateOpt.isEmpty()) {
      List<StockLimitView> list = initData();
      return list.stream().sorted(Comparator.comparing(StockLimitView::getTradeDate)).toList();
    }
    CompletableFuture.runAsync(this::getDataFromAPIAndSaveToDB, taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getSortedStockLimitView : {}", ex.getMessage());
              return null;
            });
    List<StockLimitView> dataInDB =
        stockLimitRepository.findAll().stream().map(EntityConverter::convertToModel).toList();
    dataInDB.forEach(
        item ->
            item.setSentimentScore(
                AlgorithmUtil.getStockLimitSentimentScore(
                    item.getLimitUpCount(), item.getLimitDownCount())));
    return dataInDB.stream().sorted(Comparator.comparing(StockLimitView::getTradeDate)).toList();
  }

  private List<StockLimitView> initData() {
    lock.lock();
    try {
      if (stockLimitRepository.count() > 0) {
        return stockLimitRepository.findAll().stream()
            .map(EntityConverter::convertToModel)
            .toList();
      }
      Map<LocalDate, StockLimitDownSummary> stockLimitDownSummary = getStockLimitDownSummary();
      Map<LocalDate, StockLimitUpSummary> stockLimitUpSummary = getStockLimitUpSummary();
      List<StockLimitView> list =
          stockLimitDownSummary.keySet().stream()
              .filter(stockLimitUpSummary::containsKey)
              .sorted()
              .map(
                  date -> {
                    StockLimitView stockLimitView = new StockLimitView();
                    stockLimitView.setTradeDate(date);
                    Integer limitDownCount = stockLimitDownSummary.get(date).getLimitDownCount();
                    stockLimitView.setLimitDownCount(limitDownCount);
                    Integer limitUpCount = stockLimitUpSummary.get(date).getLimitUpCount();
                    stockLimitView.setLimitUpCount(limitUpCount);
                    stockLimitView.setSentimentScore(
                        AlgorithmUtil.getStockLimitSentimentScore(limitUpCount, limitDownCount));
                    return stockLimitView;
                  })
              .toList();
      List<StockLimitEntity> stockLimitEntities =
          list.stream()
              .map(EntityConverter::convertToEntity)
              .filter(
                  stockLimitEntity ->
                      !stockLimitEntity.getTradeDate().isEqual(LocalDateUtil.getNow()))
              .toList();
      stockLimitRepository.saveAll(stockLimitEntities);
      return list;
    } finally {
      lock.unlock();
    }
  }

  private void getDataFromAPIAndSaveToDB() {
    Optional<LocalDate> maxTradeDateOpt = stockLimitRepository.findMaxTradeDate();
    if (maxTradeDateOpt.isEmpty()) {
      initData();
      return;
    }
    lock.lock();
    try {
      Optional<StockLimitEntity> byTradeDate =
          stockLimitRepository.findByTradeDate(LocalDate.now());
      if (byTradeDate.isPresent()) {
        return;
      }
      LocalDate maxTradeDate = stockLimitRepository.findMaxTradeDate().orElse(null);
      Map<LocalDate, StockLimitDownSummary> stockLimitDownSummary = getStockLimitDownSummary();
      Map<LocalDate, StockLimitUpSummary> stockLimitUpSummary = getStockLimitUpSummary();
      List<StockLimitView> list =
          stockLimitDownSummary.keySet().stream()
              .filter(stockLimitUpSummary::containsKey)
              .sorted()
              .map(
                  date -> {
                    StockLimitView stockLimitView = new StockLimitView();
                    stockLimitView.setTradeDate(date);
                    stockLimitView.setLimitDownCount(
                        stockLimitDownSummary.get(date).getLimitDownCount());
                    stockLimitView.setLimitUpCount(stockLimitUpSummary.get(date).getLimitUpCount());
                    return stockLimitView;
                  })
              .toList();
      List<StockLimitEntity> stockLimitEntities =
          list.stream()
              .filter(
                  stockLimitView -> {
                    LocalDate tradeDate = stockLimitView.getTradeDate();
                    if (tradeDate == null) {
                      return false;
                    }
                    if (tradeDate.isEqual(LocalDateUtil.getNow())
                        && LocalDateTimeUtil.isBeforeEqualStockCloseTime()) {
                      return false;
                    }
                    return tradeDate.isAfter(maxTradeDate);
                  })
              .map(EntityConverter::convertToEntity)
              .toList();
      stockLimitRepository.saveAll(stockLimitEntities);
    } finally {
      lock.unlock();
    }
  }

  public Map<LocalDate, StockLimitDownSummary> getStockLimitDownSummary() {
    LocalDate startDate = LocalDateUtil.getNow().minusDays(30);
    List<SZSECalendar> tradeCalendarInThirtyDays =
        tradeCalendarService.getSortedTradeCalendarInThirtyDays(startDate);

    List<SZSECalendar> filteredTradeCalendarInThirtyDays =
        tradeCalendarInThirtyDays.stream()
            .filter(szseCalendar -> szseCalendar.getTradeStatus() == TradeStatusEnum.TRADE)
            .sorted(Comparator.comparing(SZSECalendar::getTradeDate).reversed())
            .limit(14)
            .toList();

    Map<LocalDate, StockLimitDownSummary> stockLimitDownSummaryMap = new LinkedHashMap<>();
    for (SZSECalendar szseCalendar : filteredTradeCalendarInThirtyDays) {
      LocalDate date = szseCalendar.getTradeDate();
      StockLimitDownSummary stockLimitDownSummary =
          eastMoneyMarketStockLimitService.getStockLimitDownSummary(date);
      if (stockLimitDownSummary == null) {
        continue;
      }
      stockLimitDownSummary.setTradeDate(date);

      stockLimitDownSummaryMap.put(date, stockLimitDownSummary);
    }
    return stockLimitDownSummaryMap;
  }

  public Map<LocalDate, StockLimitUpSummary> getStockLimitUpSummary() {
    LocalDate startDate = LocalDateUtil.getNow().minusDays(30);
    List<SZSECalendar> tradeCalendarInThirtyDays =
        tradeCalendarService.getSortedTradeCalendarInThirtyDays(startDate);

    List<SZSECalendar> filteredTradeCalendarInThirtyDays =
        tradeCalendarInThirtyDays.stream()
            .filter(szseCalendar -> szseCalendar.getTradeStatus() == TradeStatusEnum.TRADE)
            .sorted(Comparator.comparing(SZSECalendar::getTradeDate).reversed())
            .limit(14)
            .toList();

    Map<LocalDate, StockLimitUpSummary> stockLimitUpSummaryMap = new LinkedHashMap<>();
    for (SZSECalendar szseCalendar : filteredTradeCalendarInThirtyDays) {
      LocalDate date = szseCalendar.getTradeDate();
      StockLimitUpSummary stockLimitUpSummary =
          eastMoneyMarketStockLimitService.getStockLimitUpSummary(date);
      if (stockLimitUpSummary == null) {
        continue;
      }
      stockLimitUpSummary.setTradeDate(date);
      stockLimitUpSummaryMap.put(date, stockLimitUpSummary);
    }
    return stockLimitUpSummaryMap;
  }
}
