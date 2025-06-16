package cn.alphacat.chinastocktrader.view.future;

import java.time.Month;
import lombok.Data;

@Data
public class DiffBetweenIFAndIndexRequestView {
  private int startYear;
  private Month startMonth;
}
