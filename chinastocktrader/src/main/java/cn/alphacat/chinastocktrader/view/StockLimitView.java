package cn.alphacat.chinastocktrader.view;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockLimitView {
  private LocalDate tradeDate;
  private Integer limitUpCount;
  private Integer limitDownCount;
}
