package cn.alphacat.chinastocktrader.controller.report;

import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.service.report.StockOpenPriceAndRiseReportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class StockOpenPriceAndRiseReportController {
  private final StockOpenPriceAndRiseReportService stockOpenPriceAndRiseReportService;

  public StockOpenPriceAndRiseReportController(
      StockOpenPriceAndRiseReportService stockOpenPriceAndRiseReportService) {
    this.stockOpenPriceAndRiseReportService = stockOpenPriceAndRiseReportService;
  }

  @RequestMapping("/stockOpenPriceAndRiseReport")
  public CommonReport getStockOpenPriceAndRiseReport() {
    return stockOpenPriceAndRiseReportService.generateStockOpenPriceAndRiseReport();
  }
}
