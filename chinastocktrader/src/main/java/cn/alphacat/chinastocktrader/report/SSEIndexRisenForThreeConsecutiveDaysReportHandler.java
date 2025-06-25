package cn.alphacat.chinastocktrader.report;

import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.model.report.SSEIndexRisenForThreeConsecutiveDaysBO;
import cn.alphacat.chinastocktrader.service.marketindex.SSEIndexHistoryService;
import cn.alphacat.chinastocktrader.service.report.CommonReportHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Component
public class SSEIndexRisenForThreeConsecutiveDaysReportHandler implements CommonReportHandler {
  private final SSEIndexHistoryService sseIndexHistoryService;

  public SSEIndexRisenForThreeConsecutiveDaysReportHandler(
      SSEIndexHistoryService sseIndexHistoryService) {
    this.sseIndexHistoryService = sseIndexHistoryService;
  }

  @Override
  public CommonReport executeReport() {
    LocalDate startDate = LocalDate.of(2015, 1, 1);
    List<MarketIndex> shanghaiIndexHistory =
        sseIndexHistoryService.getShanghaiIndexHistory(startDate);
    List<MarketIndex> sortedShanghaiIndexHistory =
        shanghaiIndexHistory.stream()
            .sorted(Comparator.comparing(MarketIndex::getTradeDate))
            .toList();

    BigDecimal preClose = sortedShanghaiIndexHistory.get(1).getClose();

    BigDecimal changePercentTheDayBeforeYesterday =
        sortedShanghaiIndexHistory.get(0).getChangePct();
    BigDecimal amountTheDayBeforeYesterday = sortedShanghaiIndexHistory.get(0).getAmount();
    BigDecimal changePercentYesterday = sortedShanghaiIndexHistory.get(1).getChangePct();
    BigDecimal amountYesterday = sortedShanghaiIndexHistory.get(1).getAmount();

    List<SSEIndexRisenForThreeConsecutiveDaysBO> result = new ArrayList<>();
    for (int i = 2; i < sortedShanghaiIndexHistory.size(); i++) {
      MarketIndex marketIndex = sortedShanghaiIndexHistory.get(i);
      if (changePercentTheDayBeforeYesterday.compareTo(new BigDecimal("0.3")) < 0
          || changePercentYesterday.compareTo(new BigDecimal("0.5")) < 0
          || marketIndex.getChangePct().compareTo(new BigDecimal("0.9")) < 0
          || amountTheDayBeforeYesterday.compareTo(amountYesterday) > 0
          || amountYesterday.compareTo(marketIndex.getAmount()) > 0) {
        preClose = marketIndex.getClose();
        changePercentTheDayBeforeYesterday = changePercentYesterday;
        amountTheDayBeforeYesterday = amountYesterday;
        changePercentYesterday = marketIndex.getChangePct();
        amountYesterday = marketIndex.getAmount();
        continue;
      }

      MarketIndex indexElementAfterFiveDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 5);
      MarketIndex indexElementAfterTenDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 10);
      MarketIndex indexElementAfterTwentyDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 20);
      MarketIndex indexElementAfterSixtyDays =
          getNthElementAfterIndex(sortedShanghaiIndexHistory, i, 60);

      BigDecimal changePercentInFutureFiveDays =
          indexElementAfterFiveDays != null
              ? indexElementAfterFiveDays
                  .getClose()
                  .subtract(marketIndex.getClose())
                  .divide(preClose, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
              : null;
      BigDecimal changePercentInFutureTenDays =
          indexElementAfterTenDays != null
              ? indexElementAfterTenDays
                  .getClose()
                  .subtract(marketIndex.getClose())
                  .divide(preClose, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
              : null;
      BigDecimal changePercentInFutureTwentyDays =
          indexElementAfterTwentyDays != null
              ? indexElementAfterTwentyDays
                  .getClose()
                  .subtract(marketIndex.getClose())
                  .divide(preClose, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
              : null;

      BigDecimal changePercentInFutureSixtyDays =
          indexElementAfterSixtyDays != null
              ? indexElementAfterSixtyDays
                  .getClose()
                  .subtract(marketIndex.getClose())
                  .divide(preClose, 6, RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(100))
              : null;

      SSEIndexRisenForThreeConsecutiveDaysBO sseIndexRisenForThreeConsecutiveDaysBO =
          SSEIndexRisenForThreeConsecutiveDaysBO.builder()
              .indexCode(marketIndex.getIndexCode())
              .tradeDate(marketIndex.getTradeDate())
              .open(marketIndex.getOpen())
              .high(marketIndex.getHigh())
              .low(marketIndex.getLow())
              .close(marketIndex.getClose())
              .volume(marketIndex.getVolume())
              .amount(marketIndex.getAmount())
              .change(marketIndex.getChange())
              .changePct(marketIndex.getChangePct())
              .preClose(preClose)
              .changePercentInFutureFiveDays(changePercentInFutureFiveDays)
              .changePercentInFutureTenDays(changePercentInFutureTenDays)
              .changePercentInFutureTwentyDays(changePercentInFutureTwentyDays)
              .changePercentInFutureSixtyDays(changePercentInFutureSixtyDays)
              .build();
      result.add(sseIndexRisenForThreeConsecutiveDaysBO);

      preClose = marketIndex.getClose();
      changePercentTheDayBeforeYesterday = changePercentYesterday;
      amountTheDayBeforeYesterday = amountYesterday;
      changePercentYesterday = marketIndex.getChangePct();
      amountYesterday = marketIndex.getAmount();
    }

    int upFiveDays = 0, downFiveDays = 0;
    int upTenDays = 0, downTenDays = 0;
    int upTwentyDays = 0, downTwentyDays = 0;
    int upSixtyDays = 0, downSixtyDays = 0;

    BigDecimal sumUpFiveDays = BigDecimal.ZERO;
    BigDecimal sumDownFiveDays = BigDecimal.ZERO;

    BigDecimal sumUpTenDays = BigDecimal.ZERO;
    BigDecimal sumDownTenDays = BigDecimal.ZERO;

    BigDecimal sumUpTwentyDays = BigDecimal.ZERO;
    BigDecimal sumDownTwentyDays = BigDecimal.ZERO;

    BigDecimal sumUpSixtyDays = BigDecimal.ZERO;
    BigDecimal sumDownSixtyDays = BigDecimal.ZERO;

    for (SSEIndexRisenForThreeConsecutiveDaysBO bo : result) {
      BigDecimal changePercentInFutureFiveDays = bo.getChangePercentInFutureFiveDays();
      if (changePercentInFutureFiveDays == null) {
        continue;
      }
      if (changePercentInFutureFiveDays.compareTo(BigDecimal.ZERO) > 0) {
        upFiveDays++;
        sumUpFiveDays = sumUpFiveDays.add(changePercentInFutureFiveDays);
      } else {
        downFiveDays++;
        sumDownFiveDays = sumDownFiveDays.add(changePercentInFutureFiveDays);
      }

      BigDecimal changePercentInFutureTenDays = bo.getChangePercentInFutureTenDays();
      if (changePercentInFutureTenDays == null) {
        continue;
      }

      if (changePercentInFutureTenDays.compareTo(BigDecimal.ZERO) > 0) {
        upTenDays++;
        sumUpTenDays = sumUpTenDays.add(changePercentInFutureTenDays);
      } else {
        downTenDays++;
        sumDownTenDays = sumDownTenDays.add(changePercentInFutureTenDays);
      }

      BigDecimal changePercentInFutureTwentyDays = bo.getChangePercentInFutureTwentyDays();
      if (changePercentInFutureTwentyDays == null) {
        continue;
      }
      if (changePercentInFutureTwentyDays.compareTo(BigDecimal.ZERO) > 0) {
        upTwentyDays++;
        sumUpTwentyDays = sumUpTwentyDays.add(changePercentInFutureTwentyDays);
      } else {
        downTwentyDays++;
        sumDownTwentyDays = sumDownTwentyDays.add(changePercentInFutureTwentyDays);
      }

      BigDecimal changePercentInFutureSixtyDays = bo.getChangePercentInFutureSixtyDays();
      if (changePercentInFutureSixtyDays == null) {
        continue;
      }
      if (changePercentInFutureSixtyDays.compareTo(BigDecimal.ZERO) > 0) {
        upSixtyDays++;
        sumUpSixtyDays = sumUpSixtyDays.add(changePercentInFutureSixtyDays);
      } else {
        downSixtyDays++;
        sumDownSixtyDays = sumDownSixtyDays.add(changePercentInFutureSixtyDays);
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
    BigDecimal avgUpSixtyDays =
        upSixtyDays > 0
            ? sumUpSixtyDays.divide(BigDecimal.valueOf(upSixtyDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    BigDecimal avgDownSixtyDays =
        downSixtyDays > 0
            ? sumDownSixtyDays.divide(BigDecimal.valueOf(downSixtyDays), 6, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    CommonReport commonReport = new CommonReport();
    commonReport.setReportName("上证指数连续三天上涨回测报告");

    LinkedHashMap<String, String> reportMeta = new LinkedHashMap<>();
    reportMeta.put("开始日期", startDate.toString());
    reportMeta.put("结束日期", LocalDate.now().toString());
    reportMeta.put("条件1", "连续三天上涨");
    reportMeta.put("条件2", "第1天涨幅>0.3%，第2天涨幅大于0.5%");
    reportMeta.put("条件3", "成交量递增");
    reportMeta.put("条件4", "第三天涨幅大于0.9%");

    reportMeta.put("次数统计", String.valueOf(result.size()));
    reportMeta.put("未来五天 - 上涨次数", String.valueOf(upFiveDays));
    reportMeta.put("未来五天 - 下跌次数", String.valueOf(downFiveDays));
    reportMeta.put("未来五天 - 上涨平均幅度", avgUpFiveDays.toString());
    reportMeta.put("未来五天 - 下跌平均幅度", avgDownFiveDays.toString());
    reportMeta.put("未来十天 - 上涨次数", String.valueOf(upTenDays));
    reportMeta.put("未来十天 - 下跌次数", String.valueOf(downTenDays));
    reportMeta.put("未来十天 - 上涨平均幅度", avgUpTenDays.toString());
    reportMeta.put("未来十天 - 下跌平均幅度", avgDownTenDays.toString());
    reportMeta.put("未来二十天 - 上涨次数", String.valueOf(upTwentyDays));
    reportMeta.put("未来二十天 - 下跌次数", String.valueOf(downTwentyDays));
    reportMeta.put("未来二十天 - 上涨平均幅度", avgUpTwentyDays.toString());
    reportMeta.put("未来二十天 - 下跌平均幅度", avgDownTwentyDays.toString());
    reportMeta.put("未来六十天 - 上涨次数", String.valueOf(upSixtyDays));
    reportMeta.put("未来六十天 - 下跌次数", String.valueOf(downSixtyDays));
    reportMeta.put("未来六十天 - 上涨平均幅度", avgUpSixtyDays.toString());
    reportMeta.put("未来六十天 - 下跌平均幅度", avgDownSixtyDays.toString());

    int index = 0;
    for (SSEIndexRisenForThreeConsecutiveDaysBO bo : result) {
      index++;
      reportMeta.put("具体日期" + index, bo.getTradeDate().toString());
    }

    commonReport.setReportData(reportMeta);
    return commonReport;
  }

  private MarketIndex getNthElementAfterIndex(List<MarketIndex> dataList, int n, int m) {
    if (dataList == null || dataList.isEmpty() || n < 0 || n >= dataList.size()) {
      return null;
    }
    int targetIndex = n + m;
    if (targetIndex >= dataList.size()) {
      return null;
    }
    return dataList.get(targetIndex);
  }
}
