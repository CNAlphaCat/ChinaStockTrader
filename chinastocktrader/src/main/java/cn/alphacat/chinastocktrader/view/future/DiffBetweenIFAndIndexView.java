package cn.alphacat.chinastocktrader.view.future;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DiffBetweenIFAndIndexView {
  private LocalDate date;
  private BigDecimal diff;
}
