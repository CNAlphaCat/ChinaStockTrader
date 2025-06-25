package cn.alphacat.chinastocktrader.model.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SSEIndexRisenForThreeConsecutiveDaysBO {
  private String indexCode;
  private LocalDate tradeDate;
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

  private BigDecimal changePercentYesterday;
  private BigDecimal amountYesterday;
  private BigDecimal changePercentTheDayBeforeYesterday;
  private BigDecimal amountTheDayBeforeYesterday;

  private BigDecimal changePercentInFutureFiveDays;
  private BigDecimal changePercentInFutureTenDays;
  private BigDecimal changePercentInFutureTwentyDays;
}
