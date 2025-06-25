package cn.alphacat.chinastocktrader.service.report;

import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.report.SSEIndexRisenForThreeConsecutiveDaysReportHandler;
import cn.alphacat.chinastocktrader.repository.stock.StockKlineCacheRepository;
import cn.alphacat.chinastocktrader.service.marketindex.SSEIndexHistoryService;
import org.springframework.stereotype.Service;

@Service
public class CommonReportFactory {
  private final StockKlineCacheRepository stockKlineCacheRepository;
  private final SSEIndexHistoryService sseIndexHistoryService;

  public CommonReportFactory(
      final StockKlineCacheRepository stockKlineCacheRepository,
      final SSEIndexHistoryService sseIndexHistoryService) {
    this.stockKlineCacheRepository = stockKlineCacheRepository;
    this.sseIndexHistoryService = sseIndexHistoryService;
  }

  public CommonReport generateCommonReport(CommonReportEnums reportType) {
    CommonReportHandler commonReportHandler = getCommonReportService(reportType);
    if (commonReportHandler == null) {
      return null;
    }
    return commonReportHandler.executeReport();
  }

  public CommonReportHandler getCommonReportService(CommonReportEnums reportType) {
    return switch (reportType) {
      case CommonReportEnums.STOCK_OPEN_PRICE_AND_RISE ->
          new StockOpenPriceAndRiseReportHandler(stockKlineCacheRepository);
      case CommonReportEnums.SSE_INDEX_RISEN_FOR_THREE_CONSECUTIVE_DAYS ->
          new SSEIndexRisenForThreeConsecutiveDaysReportHandler(sseIndexHistoryService);
      default -> null;
    };
  }
}
