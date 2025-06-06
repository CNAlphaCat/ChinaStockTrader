package cn.alphacat.chinastocktrader.controller.stock;

import cn.alphacat.chinastockdata.model.stock.StockInfo;
import cn.alphacat.chinastocktrader.service.stock.StockInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock/info")
public class StockInfoController {
  private final StockInfoService stockInfoService;

  public StockInfoController(final StockInfoService stockInfoService) {
    this.stockInfoService = stockInfoService;
  }

  @RequestMapping("/list")
  public List<StockInfo> getStockInfoList() {
    return stockInfoService.getStockInfoList();
  }
}
