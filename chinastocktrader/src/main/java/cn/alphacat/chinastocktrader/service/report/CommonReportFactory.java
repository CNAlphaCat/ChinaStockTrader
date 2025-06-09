package cn.alphacat.chinastocktrader.service.report;

import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.repository.stock.StockKlineCacheRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonReportFactory {
  private final StockKlineCacheRepository stockKlineCacheRepository;

  public CommonReportFactory(final StockKlineCacheRepository stockKlineCacheRepository) {
    this.stockKlineCacheRepository = stockKlineCacheRepository;
  }

  public CommonReport generateCommonReport(CommonReportEnums reportType) {
    CommonReportService commonReportService = getCommonReportService(reportType);
    if (commonReportService == null) {
      return null;
    }
    return commonReportService.executeReport();
  }

  public CommonReportService getCommonReportService(CommonReportEnums reportType) {
    switch (reportType) {
      case CommonReportEnums.STOCK_OPEN_PRICE_AND_RISE:
        return new StockOpenPriceAndRiseReportService(stockKlineCacheRepository);
      default:
        return null;
    }
  }
}
