package cn.alphacat.chinastocktrader.service.stock;

import cn.alphacat.chinastockdata.model.stock.StockInfo;
import cn.alphacat.chinastockdata.stock.StockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockInfoService {
  private final StockService stockService;

  public StockInfoService(StockService stockService) {
    this.stockService = stockService;
  }

  public List<StockInfo> getStockInfoList() {
    return stockService.getStockInfoList();
  }
}
