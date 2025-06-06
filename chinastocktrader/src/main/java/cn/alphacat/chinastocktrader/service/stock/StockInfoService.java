package cn.alphacat.chinastocktrader.service.stock;

import cn.alphacat.chinastockdata.model.stock.StockInfo;
import cn.alphacat.chinastockdata.stock.StockService;
import cn.alphacat.chinastocktrader.repository.stock.StockInfoRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockInfoService {
  private final StockService stockService;
  private final StockInfoRepository stockInfoRepository;

  public StockInfoService(
      final StockService stockService, final StockInfoRepository stockInfoRepository) {
    this.stockService = stockService;
    this.stockInfoRepository = stockInfoRepository;
  }

  public List<StockInfo> getStockInfoList() {
    List<StockInfo> stockInfoList = stockService.getStockInfoList();
    stockInfoList.stream()
        .filter(StockInfoService::filter)
        .forEach(
            stockInfo -> {
              if (!stockInfoRepository.existsByStockCode(stockInfo.getStockCode())) {
                stockInfoRepository.save(EntityConverter.convertToEntity(stockInfo));
              }
            });
    return stockInfoList;
  }

  private static boolean filter(StockInfo stockInfo) {
    String stockName = stockInfo.getStockName();
    if (stockName.startsWith("ST")) {
      return false;
    }
    if (stockName.startsWith("C")) {
      return false;
    }
    if (stockName.startsWith("*")) {
      return false;
    }
    if (stockName.startsWith("N")) {
      return false;
    }
    if(stockName.startsWith("XD")){
      return false;
    }
    return true;
  }
}
