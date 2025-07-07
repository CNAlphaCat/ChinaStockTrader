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

  private BigDecimal recentlyMonthDiff;
  private BigDecimal ifRecentlyMonthOpenPrice;
  private BigDecimal ifRecentlyMonthClosePrice;
  private BigDecimal ifRecentlyMonthHighPrice;
  private BigDecimal ifRecentlyMonthLowPrice;
  private BigDecimal ifRecentlyMonthVolume;
  private BigDecimal ifRecentlyMonthAmount;
  private String ifRecentlyMonthCode;

  private BigDecimal csi300ClosePrice;
}
