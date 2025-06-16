package cn.alphacat.chinastocktrader.view.future;

import lombok.Data;

import java.time.Month;

@Data
public class DiffBetweenIMAndIndexRequestView {
  private int startYear;
  private Month startMonth;
}
