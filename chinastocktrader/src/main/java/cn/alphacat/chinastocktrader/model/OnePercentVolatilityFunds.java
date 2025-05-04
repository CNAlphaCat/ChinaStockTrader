package cn.alphacat.chinastocktrader.model;

import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
public class OnePercentVolatilityFunds {
  private BigDecimal displayFunds;
  private BigDecimal amount;
  private LocalDate date;
  private BigDecimal volatility;

  public OnePercentVolatilityFunds(MarketIndex marketIndex) {
    BigDecimal amount = marketIndex.getAmount();
    BigDecimal close = marketIndex.getClose();
    BigDecimal change = marketIndex.getChange();
    BigDecimal preClose = close.subtract(change).setScale(2, RoundingMode.HALF_UP);

    BigDecimal high = marketIndex.getHigh();
    BigDecimal low = marketIndex.getLow();

    this.volatility =
        high.subtract(low)
            .divide(preClose, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    this.date = marketIndex.getTradeDate();

    this.amount = amount;
    this.displayFunds =
        amount
            .divide(volatility, 2, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(1_000_000_000), 2, RoundingMode.HALF_UP);
  }
}
