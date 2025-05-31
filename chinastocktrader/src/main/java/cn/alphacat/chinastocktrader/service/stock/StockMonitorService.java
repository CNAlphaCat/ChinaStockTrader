package cn.alphacat.chinastocktrader.service.stock;

import cn.alphacat.chinastockdata.model.stock.StockMin;
import cn.alphacat.chinastockdata.stock.StockService;
import cn.alphacat.chinastocktrader.model.StockMinBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    return StockMinBO.builder()
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
        .changePercentInThreeMinutes(changePercentInThreeMinutes)
        .build();
  }

  private BigDecimal getChangePercentInNMinutes(ArrayList<StockMin> stockMinList, int n) {
    stockMinList.sort(Comparator.comparing(StockMin::getTradeTime));
    StockMin last = stockMinList.getLast();
    BigDecimal preOpen = last.getOpenPrice();
    if (preOpen == null) {
      return BigDecimal.ZERO;
    }

    int secondLastIndex = stockMinList.size() - 2;
    for (int i = secondLastIndex; i >= 0 && i >= stockMinList.size() - n; i--) {
      StockMin current = stockMinList.get(i);
      LocalTime tradeTime = current.getTradeTime().toLocalTime();
      if (tradeTime.isBefore(LocalTime.of(9, 30))) {
        continue;
      }
      preOpen = current.getOpenPrice() == null ? preOpen : current.getOpenPrice();
    }
    return last.getClosePrice()
        .subtract(preOpen)
        .divide(preOpen, 8, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
  }
}
