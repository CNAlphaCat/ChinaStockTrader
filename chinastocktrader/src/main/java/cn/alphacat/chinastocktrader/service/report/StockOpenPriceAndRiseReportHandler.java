package cn.alphacat.chinastocktrader.service.report;

import cn.alphacat.chinastocktrader.entity.stock.StockKlineCacheEntity;
import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.repository.stock.StockKlineCacheRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class StockOpenPriceAndRiseReportHandler implements CommonReportHandler {
  private final StockKlineCacheRepository stockKlineCacheRepository;

  private static final String REPORT_TITLE = "股票开盘价与最终涨幅7%回测报告";

  public StockOpenPriceAndRiseReportHandler(StockKlineCacheRepository stockKlineCacheRepository) {
    this.stockKlineCacheRepository = stockKlineCacheRepository;
  }

  @Override
  public CommonReport executeReport() {
    List<StockKlineCacheEntity> all = stockKlineCacheRepository.findAll();

    int changePercentGreaterThanSevenPercentCount = 0;
    int openGreaterThanPrePriceSevenPercentCount = 0;
    int openBetweenThreePercentAndSevenPercentCount = 0;
    int openBetweenTwoPercentAndThreePercentCount = 0;
    int openBetweenOnePercentAndTwoPercentCount = 0;
    int openBetweenZeroPercentAndOnePercentCount = 0;
    int openBetweenMinusOnePercentAndZeroPercentCount = 0;
    int openBetweenMinusTwoPercentAndMinusOnePercentCount = 0;
    int openBetweenMinusThreePercentAndMinusTwoPercentCount = 0;
    int openLessThanMinusThreePercent = 0;

    for (StockKlineCacheEntity stockKlineCacheEntity : all) {
      BigDecimal changePercent = stockKlineCacheEntity.getChangePercent();
      if (changePercent.compareTo(BigDecimal.valueOf(7)) < 0) {
        continue;
      }
      changePercentGreaterThanSevenPercentCount++;

      BigDecimal open = stockKlineCacheEntity.getOpen();
      BigDecimal preKPrice = stockKlineCacheEntity.getPreKPrice();
      BigDecimal openChangePercent =
          open.subtract(preKPrice)
              .divide(preKPrice, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      if (openChangePercent.compareTo(BigDecimal.valueOf(7)) > 0) {
        openGreaterThanPrePriceSevenPercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(3)) > 0) {
        openBetweenThreePercentAndSevenPercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(2)) > 0) {
        openBetweenTwoPercentAndThreePercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(1)) > 0) {
        openBetweenOnePercentAndTwoPercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(0)) > 0) {
        openBetweenZeroPercentAndOnePercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(-1)) > 0) {
        openBetweenMinusOnePercentAndZeroPercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(-2)) > 0) {
        openBetweenMinusTwoPercentAndMinusOnePercentCount++;
        continue;
      }
      if (openChangePercent.compareTo(BigDecimal.valueOf(-3)) > 0) {
        openBetweenMinusThreePercentAndMinusTwoPercentCount++;
        continue;
      }
      openLessThanMinusThreePercent++;
    }

    LinkedHashMap<String, String> reportData = new LinkedHashMap<>();
    reportData.put("时间周期", "最近一年");
    reportData.put("总K线数量", String.valueOf(all.size()));
    reportData.put("股票范围", "A股排除北交所；股票名字N，C，ST与*开头；股票代码2与9开头的股票");
    reportData.put("统计区间界限界定原则", "左开右闭区间");
    reportData.put("涨幅大于7%的K线数量", String.valueOf(changePercentGreaterThanSevenPercentCount));
    reportData.put("开盘价高于前一日收盘价7%的K线数量", String.valueOf(openGreaterThanPrePriceSevenPercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在3%~7%之间的K线数量", String.valueOf(openBetweenThreePercentAndSevenPercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在2%~3%之间的K线数量", String.valueOf(openBetweenTwoPercentAndThreePercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在1%~2%之间的K线数量", String.valueOf(openBetweenOnePercentAndTwoPercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在0%~1%之间的K线数量", String.valueOf(openBetweenZeroPercentAndOnePercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在-1%~0%之间的K线数量", String.valueOf(openBetweenMinusOnePercentAndZeroPercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在-2%~-1%之间的K线数量",
        String.valueOf(openBetweenMinusTwoPercentAndMinusOnePercentCount));
    reportData.put(
        "开盘价对比前一日收盘价在-3%~-2%之间的K线数量",
        String.valueOf(openBetweenMinusThreePercentAndMinusTwoPercentCount));
    reportData.put("开盘价对比前一日收盘价小于-3%的K线数量", String.valueOf(openLessThanMinusThreePercent));

    CommonReport commonReport = new CommonReport();
    commonReport.setReportName(REPORT_TITLE);
    commonReport.setReportData(reportData);
    return commonReport;
  }
}
