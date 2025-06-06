package cn.alphacat.chinastocktrader.controller.stock;

import cn.alphacat.chinastocktrader.model.stock.FiveMinutesKlineAnalysis;
import cn.alphacat.chinastocktrader.service.stock.StockAnalyzeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock/analyze")
public class StockAnalyzeController {
  private final StockAnalyzeService stockAnalyzeService;

  public StockAnalyzeController(final StockAnalyzeService stockAnalyzeService) {
    this.stockAnalyzeService = stockAnalyzeService;
  }

  @RequestMapping("/kline/5minutes/{stockCode}")
  public FiveMinutesKlineAnalysis analyzeStock(@PathVariable String stockCode) {
    return stockAnalyzeService.analyzeStock(stockCode);
  }
}
