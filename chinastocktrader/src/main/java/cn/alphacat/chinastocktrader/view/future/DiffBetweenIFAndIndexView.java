package cn.alphacat.chinastocktrader.view.future;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DiffBetweenIFAndIndexView {
  private LocalDate date;

  private BigDecimal mainDiff;
  private BigDecimal ifMainOpenPrice;
  private BigDecimal ifMainClosePrice;
  private BigDecimal ifMainHighPrice;
  private BigDecimal ifMainLowPrice;
  private BigDecimal ifMainVolume;
  private BigDecimal ifMainAmount;
  private String ifMainCode;

  private BigDecimal nextMonthDiff;
  private BigDecimal ifNextMonthOpenPrice;
  private BigDecimal ifNextMonthClosePrice;
  private BigDecimal ifNextMonthHighPrice;
  private BigDecimal ifNextMonthLowPrice;
  private BigDecimal ifNextMonthVolume;
  private BigDecimal ifNextMonthAmount;
  private String ifNextMonthCode;

  private BigDecimal csi300ClosePrice;

  private BigDecimal delta;
}
