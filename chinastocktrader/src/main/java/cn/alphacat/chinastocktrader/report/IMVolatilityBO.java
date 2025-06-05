package cn.alphacat.chinastocktrader.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class IMVolatilityBO {
  private LocalDate date;
  private BigDecimal highLowRange;
  private BigDecimal percentile;
}
