package cn.alphacat.chinastocktrader.view.future;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DiffBetweenIFAndIndexView {
  private LocalDate date;
  private BigDecimal diff;
  private BigDecimal ifOpenPrice;
  private BigDecimal ifClosePrice;
  private BigDecimal ifHighPrice;
  private BigDecimal ifLowPrice;
  private BigDecimal ifVolume;
  private BigDecimal ifAmount;
  private BigDecimal csi300ClosePrice;
  private String ifCode;
  private BigDecimal delta;
}
