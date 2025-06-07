package cn.alphacat.chinastocktrader.controller.stock;

import cn.alphacat.chinastocktrader.service.stock.StockCacheService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock/data")
public class StockKlineDataController {
  public StockKlineDataController(final StockCacheService stockCacheService) {
    this.stockKlineDataService = stockCacheService;
  }

  private final StockCacheService stockKlineDataService;

  @RequestMapping("/generate")
  public void generateStockKlineData() {
    stockKlineDataService.generateStockKlineCache();
  }
}
