package cn.alphacat.chinastocktrader.view.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockLimitView {
  private LocalDate tradeDate;
  private Integer limitUpCount;
  private Integer limitDownCount;
  private BigDecimal sentimentScore;
}
