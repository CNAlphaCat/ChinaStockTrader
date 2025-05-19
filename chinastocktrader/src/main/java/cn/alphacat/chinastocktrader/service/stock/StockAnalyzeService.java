package cn.alphacat.chinastocktrader.service.stock;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.model.stock.StockKline;
import cn.alphacat.chinastockdata.model.stock.StockKlineData;
import cn.alphacat.chinastockdata.stock.StockService;
import cn.alphacat.chinastockdata.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.model.stock.FiveMinutesKlineAnalysis;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class StockAnalyzeService {
  private final StockService stockService;

  private static final EastMoneyQTKlineTypeEnum EAST_MONEY_QT_KLINE_TYPE_FIVE_MINUTES =
      EastMoneyQTKlineTypeEnum.FIVE_MINUTE;

  public StockAnalyzeService(StockService stockService) {
    this.stockService = stockService;
  }

  private static final int MAX_KLINE_COUNT = 5;

  public FiveMinutesKlineAnalysis analyzeStock(String stockCode) {
    LocalDate now = LocalDateUtil.getNow();
    LocalDate startDate = now.minusDays(30);

    StockKlineData stockKlineDataInFiveMinutes =
        stockService.getStockKlineData(stockCode, EAST_MONEY_QT_KLINE_TYPE_FIVE_MINUTES, startDate);

    if (stockKlineDataInFiveMinutes == null) {
      return FiveMinutesKlineAnalysis.builder().build();
    }

    int increaseFiveMinuteKlineCount = 0;
    int decreaseFiveMinuteCount = 0;
    LocalDateTime startDateTime = startDate.atTime(9, 30);
    LocalDateTime endDateTime = now.atTime(15, 0);

    List<StockKline> kLinesInFiveMinuteList = stockKlineDataInFiveMinutes.getKLines();
    PriorityQueue<StockKline> minHeap =
        new PriorityQueue<>(Comparator.comparing(StockKline::getAmount));

    for (StockKline kLineInFiveMinute : kLinesInFiveMinuteList) {
      if (kLineInFiveMinute.getDateTime().isBefore(startDateTime)) {
        startDateTime = kLineInFiveMinute.getDateTime();
      }
      if (kLineInFiveMinute.getDateTime().isAfter(endDateTime)) {
        endDateTime = kLineInFiveMinute.getDateTime();
      }
      if (kLineInFiveMinute.getChangePercent().compareTo(BigDecimal.ZERO) > 0) {
        increaseFiveMinuteKlineCount++;
      } else if (kLineInFiveMinute.getChangePercent().compareTo(BigDecimal.ZERO) < 0) {
        decreaseFiveMinuteCount++;
      }
      if (minHeap.size() < MAX_KLINE_COUNT + 1) {
        minHeap.offer(kLineInFiveMinute);
      } else if (kLineInFiveMinute.getAmount().compareTo(minHeap.peek().getAmount()) > 0) {
        minHeap.poll();
        minHeap.offer(kLineInFiveMinute);
      }
    }

    List<StockKline> topKlinesDesc =
        minHeap.stream().sorted(Comparator.comparing(StockKline::getAmount).reversed()).toList();

    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 1; i < topKlinesDesc.size(); i++) {
      sum = sum.add(topKlinesDesc.get(i).getAmount());
    }

    BigDecimal expectedRisingAmountInOneMinute = BigDecimal.ZERO;
    if (sum.compareTo(BigDecimal.ZERO) > 0 && topKlinesDesc.size() > 1) {
      expectedRisingAmountInOneMinute =
          sum.divide(BigDecimal.valueOf(MAX_KLINE_COUNT), RoundingMode.HALF_UP)
              .divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);
    }

    StockKlineData stockKlineDataInThirtyMinutes =
        stockService.getStockKlineData(
            stockCode, EastMoneyQTKlineTypeEnum.THIRTY_MINUTE, startDate);
    int increaseThirtyMinuteKlineCount = 0;
    int decreaseThirtyMinuteKlineCount = 0;
    for (StockKline kLineInThirtyMinute : stockKlineDataInThirtyMinutes.getKLines()) {
      if (kLineInThirtyMinute.getChangePercent().compareTo(BigDecimal.ZERO) > 0) {
        increaseThirtyMinuteKlineCount++;
      } else if (kLineInThirtyMinute.getChangePercent().compareTo(BigDecimal.ZERO) < 0) {
        decreaseThirtyMinuteKlineCount++;
      }
    }

    return FiveMinutesKlineAnalysis.builder()
        .stockCode(stockCode)
        .startTime(startDateTime)
        .endTime(endDateTime)
        .increaseFiveMinuteKlineCount(increaseFiveMinuteKlineCount)
        .decreaseFiveMinuteKlineCount(decreaseFiveMinuteCount)
        .increaseThirtyMinuteKlineCount(increaseThirtyMinuteKlineCount)
        .decreaseThirtyMinuteKlineCount(decreaseThirtyMinuteKlineCount)
        .topVolumeKlines(minHeap)
        .expectedRisingAmountInOneMinute(expectedRisingAmountInOneMinute)
        .displayRisingAmountInOneMinute(
            expectedRisingAmountInOneMinute.divide(
                    BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)
                + "万元")
        .build();
  }
}
