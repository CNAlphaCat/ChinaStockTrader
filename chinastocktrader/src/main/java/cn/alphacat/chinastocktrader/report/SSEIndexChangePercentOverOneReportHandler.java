package cn.alphacat.chinastocktrader.report;

import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.model.report.ChangPercentOverOneBO;
import cn.alphacat.chinastocktrader.service.marketindex.SSEIndexHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class SSEIndexChangePercentOverOneReportHandler {
  private final SSEIndexHistoryService sseIndexHistoryService;

  public SSEIndexChangePercentOverOneReportHandler(SSEIndexHistoryService sseIndexHistoryService) {
    this.sseIndexHistoryService = sseIndexHistoryService;
  }

  public List<ChangPercentOverOneBO> report(BigDecimal percent) {
    LocalDate startDate = LocalDate.of(2017, 1, 1);
    List<MarketIndex> shanghaiIndexHistory =
        sseIndexHistoryService.getShanghaiIndexHistory(startDate);
    List<MarketIndex> sortedShanghaiIndexHistory =
        shanghaiIndexHistory.stream()
            .sorted(Comparator.comparing(MarketIndex::getTradeDate))
            .toList();
    BigDecimal preClose = sortedShanghaiIndexHistory.get(0).getClose();

    List<ChangPercentOverOneBO> result = new ArrayList<>();

    for (int i = 1; i < sortedShanghaiIndexHistory.size(); i++) {
      MarketIndex marketIndex = sortedShanghaiIndexHistory.get(i);
      BigDecimal openChangePercent =
          marketIndex
              .getOpen()
              .subtract(preClose)
              .divide(preClose, 6, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      if (openChangePercent.compareTo(percent) < 0) {
        preClose = marketIndex.getClose();
        continue;
      }

      MarketIndex indexElementAfterFiveDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 5);
      MarketIndex indexElementAfterTenDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 10);
      MarketIndex indexElementAfterTwentyDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 20);

      BigDecimal changePercentInFutureFiveDays =
          indexElementAfterFiveDays
              .getClose()
              .subtract(marketIndex.getOpen())
              .divide(preClose, 6, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      BigDecimal changePercentInFutureTenDays =
          indexElementAfterTenDays
              .getClose()
              .subtract(marketIndex.getOpen())
              .divide(preClose, 6, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      BigDecimal changePercentInFutureTwentyDays =
          indexElementAfterTwentyDays
              .getClose()
              .subtract(marketIndex.getOpen())
              .divide(preClose, 6, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));

      ChangPercentOverOneBO changPercentOverOneBO =
          ChangPercentOverOneBO.builder()
              .indexCode(marketIndex.getIndexCode())
              .tradeDate(marketIndex.getTradeDate())
              .tradeTime(marketIndex.getTradeTime())
              .open(marketIndex.getOpen())
              .high(marketIndex.getHigh())
              .low(marketIndex.getLow())
              .close(marketIndex.getClose())
              .volume(marketIndex.getVolume())
              .amount(marketIndex.getAmount())
              .change(marketIndex.getChange())
              .changePct(marketIndex.getChangePct())
              .preClose(preClose)
              .openChangePercent(openChangePercent)
              .changePercentInFutureFiveDays(changePercentInFutureFiveDays)
              .changePercentInFutureTenDays(changePercentInFutureTenDays)
              .changePercentInFutureTwentyDays(changePercentInFutureTwentyDays)
              .build();

      result.add(changPercentOverOneBO);

      preClose = marketIndex.getClose();
    }

    int upFiveDays = 0, downFiveDays = 0;
    int upTenDays = 0, downTenDays = 0;
    int upTwentyDays = 0, downTwentyDays = 0;
    int closeGreaterThanOpenCount = 0;

    BigDecimal sumUpFiveDays = BigDecimal.ZERO;
    BigDecimal sumDownFiveDays = BigDecimal.ZERO;

    BigDecimal sumUpTenDays = BigDecimal.ZERO;
    BigDecimal sumDownTenDays = BigDecimal.ZERO;

    BigDecimal sumUpTwentyDays = BigDecimal.ZERO;
    BigDecimal sumDownTwentyDays = BigDecimal.ZERO;

    for (ChangPercentOverOneBO bo : result) {

      if (bo.getChangePercentInFutureFiveDays().compareTo(BigDecimal.ZERO) > 0) {
        upFiveDays++;
        sumUpFiveDays = sumUpFiveDays.add(bo.getChangePercentInFutureFiveDays());
      } else {
        downFiveDays++;
        sumDownFiveDays = sumDownFiveDays.add(bo.getChangePercentInFutureFiveDays());
      }

      if (bo.getChangePercentInFutureTenDays().compareTo(BigDecimal.ZERO) > 0) {
        upTenDays++;
        sumUpTenDays = sumUpTenDays.add(bo.getChangePercentInFutureTenDays());
      } else {
        downTenDays++;
        sumDownTenDays = sumDownTenDays.add(bo.getChangePercentInFutureTenDays());
      }

      if (bo.getChangePercentInFutureTwentyDays().compareTo(BigDecimal.ZERO) > 0) {
        upTwentyDays++;
        sumUpTwentyDays = sumUpTwentyDays.add(bo.getChangePercentInFutureTwentyDays());
      } else {
        downTwentyDays++;
        sumDownTwentyDays = sumDownTwentyDays.add(bo.getChangePercentInFutureTwentyDays());
      }

      if (bo.getClose().compareTo(bo.getOpen()) > 0) {
        closeGreaterThanOpenCount++;
      }
    }

    BigDecimal avgUpFiveDays =
        upFiveDays > 0
            ? sumUpFiveDays.divide(BigDecimal.valueOf(upFiveDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    BigDecimal avgDownFiveDays =
        downFiveDays > 0
            ? sumDownFiveDays.divide(BigDecimal.valueOf(downFiveDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    BigDecimal avgUpTenDays =
        upTenDays > 0
            ? sumUpTenDays.divide(BigDecimal.valueOf(upTenDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    BigDecimal avgDownTenDays =
        downTenDays > 0
            ? sumDownTenDays.divide(BigDecimal.valueOf(downTenDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    BigDecimal avgUpTwentyDays =
        upTwentyDays > 0
            ? sumUpTwentyDays.divide(BigDecimal.valueOf(upTwentyDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    BigDecimal avgDownTwentyDays =
        downTwentyDays > 0
            ? sumDownTwentyDays.divide(BigDecimal.valueOf(downTwentyDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    log.info("从2017年月1日开始");
    log.info("计算高开大于等于 {} % 的上证指数的未来表现情况", percent);
    log.info("共{}次", result.size());

    log.info("收盘价大于开盘价的次数：{} 次", closeGreaterThanOpenCount);

    log.info("未来五天 - 上涨次数：{}, 下跌次数：{}", upFiveDays, downFiveDays);
    log.info("五天后收盘价>高开开盘价时，未来涨幅表现平均值（五天）：{}%", avgUpFiveDays);
    log.info("五天后收盘价<高开开盘价时，未来涨幅表现平均值（五天）：{}%", avgDownFiveDays);

    log.info("未来十天 - 上涨次数：{}, 下跌次数：{}", upTenDays, downTenDays);
    log.info("十天后收盘价>高开开盘价时，未来涨幅表现平均值（十天）：{}%", avgUpTenDays);
    log.info("十天后收盘价<高开开盘价时，未来涨幅表现平均值（十天）：{}%", avgDownTenDays);

    log.info("未来二十天 - 上涨次数：{}, 下跌次数：{}", upTwentyDays, downTwentyDays);
    log.info("二十天后收盘价>高开开盘价时，未来涨幅表现平均值（二十天）：{}%", avgUpTwentyDays);
    log.info("二十天后收盘价<高开开盘价时，未来涨幅表现平均值（二十天）：{}%", avgDownTwentyDays);

    return result;
  }

  private MarketIndex getNthElementAfterIndex(List<MarketIndex> dataList, int n, int m) {
    if (dataList == null || dataList.isEmpty() || n < 0 || n >= dataList.size()) {
      return null;
    }
    int targetIndex = n + m;
    if (targetIndex >= dataList.size()) {
      return dataList.getLast();
    }
    return dataList.get(targetIndex);
  }
}
