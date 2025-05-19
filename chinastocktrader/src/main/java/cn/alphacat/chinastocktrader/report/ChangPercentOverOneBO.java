package cn.alphacat.chinastocktrader.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ChangPercentOverOneBO {
  private String indexCode;
  private LocalDate tradeDate;
  private LocalDateTime tradeTime;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private BigDecimal volume;
  private BigDecimal amount;
  private BigDecimal change;
  private BigDecimal changePct;

  private BigDecimal preClose;
  private BigDecimal openChangePercent;
  private BigDecimal changePercentInFutureFiveDays;
  private BigDecimal changePercentInFutureTenDays;
  private BigDecimal changePercentInFutureTwentyDays;
}
