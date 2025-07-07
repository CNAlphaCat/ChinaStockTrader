package cn.alphacat.chinastocktrader.view.future;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiffBetweenIMAndIndexView {
  private LocalDate date;

  private BigDecimal mainDiff;
  private BigDecimal imMainOpenPrice;
  private BigDecimal imMainClosePrice;
  private BigDecimal imMainHighPrice;
  private BigDecimal imMainLowPrice;
  private BigDecimal imMainVolume;
  private BigDecimal imMainAmount;
  private String imMainCode;

  private BigDecimal recentlyMonthDiff;
  private BigDecimal imRecentlyMonthOpenPrice;
  private BigDecimal imRecentlyMonthClosePrice;
  private BigDecimal imRecentlyMonthHighPrice;
  private BigDecimal imRecentlyMonthLowPrice;
  private BigDecimal imRecentlyMonthVolume;
  private BigDecimal imRecentlyMonthAmount;
  private String imRecentlyMonthCode;

  private BigDecimal csi1000ClosePrice;
}
