package cn.alphacat.chinastocktrader.controller.stock;

import cn.alphacat.chinastocktrader.model.StockMinBO;
import cn.alphacat.chinastocktrader.service.stock.StockMonitorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockMonitorController {

  private final StockMonitorService stockMonitorService;

  public StockMonitorController(final StockMonitorService stockMonitorService) {
    this.stockMonitorService = stockMonitorService;
  }

  @PostMapping("/monitor")
  public List<StockMinBO> monitor(@RequestBody List<String> stockCodeList) {
    return stockMonitorService.monitorStockList(stockCodeList);
  }
}
