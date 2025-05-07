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

    StockKlineData stockKlineData =
        stockService.getStockKlineData(stockCode, EAST_MONEY_QT_KLINE_TYPE_FIVE_MINUTES, startDate);

    if (stockKlineData == null) {
      return FiveMinutesKlineAnalysis.builder().build();
    }

    int increaseCount = 0;
    int decreaseCount = 0;
    LocalDateTime startDateTime = startDate.atTime(9, 30);
    LocalDateTime endDateTime = now.atTime(15, 0);

    List<StockKline> kLines = stockKlineData.getKLines();
    PriorityQueue<StockKline> minHeap =
        new PriorityQueue<>(Comparator.comparing(StockKline::getAmount));

    for (StockKline kLine : kLines) {
      if (kLine.getDateTime().isBefore(startDateTime)) {
        startDateTime = kLine.getDateTime();
      }
      if (kLine.getDateTime().isAfter(endDateTime)) {
        endDateTime = kLine.getDateTime();
      }
      if (kLine.getChangePercent().compareTo(BigDecimal.ZERO) > 0) {
        increaseCount++;
      } else if (kLine.getChangePercent().compareTo(BigDecimal.ZERO) < 0) {
        decreaseCount++;
      }
      if (minHeap.size() < MAX_KLINE_COUNT + 1) {
        minHeap.offer(kLine);
      } else if (kLine.getAmount().compareTo(minHeap.peek().getAmount()) > 0) {
        minHeap.poll();
        minHeap.offer(kLine);
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

    return FiveMinutesKlineAnalysis.builder()
        .stockCode(stockCode)
        .startTime(startDateTime)
        .endTime(endDateTime)
        .increaseCount(increaseCount)
        .decreaseCount(decreaseCount)
        .topVolumeKlines(minHeap)
        .expectedRisingAmountInOneMinute(expectedRisingAmountInOneMinute)
        .displayRisingAmountInOneMinute(
            expectedRisingAmountInOneMinute.divide(
                    BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)
                + "万元")
        .build();
  }
}
