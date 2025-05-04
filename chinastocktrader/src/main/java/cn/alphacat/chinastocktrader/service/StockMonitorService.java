package cn.alphacat.chinastocktrader.service;

import cn.alphacat.chinastockdata.model.stock.StockMin;
import cn.alphacat.chinastockdata.stock.StockService;
import cn.alphacat.chinastocktrader.model.StockMinBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockMonitorService {
  private final StockService stockService;

  public StockMonitorService(final StockService stockService) {
    this.stockService = stockService;
  }

  public List<StockMinBO> monitorStockList(List<String> stockCodeList) {
    List<StockMinBO> stockMinBOList = new ArrayList<>();
    for (String stockCode : stockCodeList) {
      ArrayList<StockMin> stockMinList = stockService.getStockMin(stockCode);

      if (stockMinList == null) {
        return new ArrayList<>();
      }
      StockMinBO stockMinBO = buildStockMinBO(stockMinList);
      stockMinBOList.add(stockMinBO);
    }
    return stockMinBOList;
  }

  private StockMinBO buildStockMinBO(ArrayList<StockMin> stockMinList) {
    if (stockMinList.isEmpty()) {
      return null;
    }
    StockMin last = stockMinList.getLast();
    BigDecimal changePercentInThreeMinutes = getChangePercentInNMinutes(stockMinList, 3);

    StockMinBO stockMinBO =
        StockMinBO.builder()
            .stockCode(last.getStockCode())
            .tradeTime(last.getTradeTime())
            .openPrice(last.getOpenPrice())
            .highPrice(last.getHighPrice())
            .lowPrice(last.getLowPrice())
            .closePrice(last.getClosePrice())
            .change(last.getChange())
            .changePercent(last.getChangePercent())
            .volume(last.getVolume())
            .averagePrice(last.getAveragePrice())
            .amount(last.getAmount())
            .build();
    stockMinBO.setChangePercentInThreeMinutes(changePercentInThreeMinutes);
    return stockMinBO;
  }

  private BigDecimal getChangePercentInNMinutes(ArrayList<StockMin> stockMinList, int n) {
    StockMin last = stockMinList.getLast();
    BigDecimal preChangePercent = BigDecimal.ZERO;
    int secondLastIndex = stockMinList.size() - 2;
    for (int i = secondLastIndex; i >= 0 && i >= stockMinList.size() - n; i--) {
      StockMin current = stockMinList.get(i);
      preChangePercent = current.getChangePercent();
    }
    return last.getChangePercent().subtract(preChangePercent);
  }
}
