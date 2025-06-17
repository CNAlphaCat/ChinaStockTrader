package cn.alphacat.chinastocktrader.view.future;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiffBetweenIMAndIndexView {
  private LocalDate date;
  private BigDecimal diff;
  private BigDecimal imPrice;
  private BigDecimal indexPrice;
  private String imCode;
}
