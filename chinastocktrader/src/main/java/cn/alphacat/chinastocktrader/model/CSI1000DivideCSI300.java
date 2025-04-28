package cn.alphacat.chinastocktrader.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CSI1000DivideCSI300 {
  private LocalDate date;
  private BigDecimal dividedValue;
}
