package cn.alphacat.chinastocktrader.view;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FutureHistoryView {
  private LocalDate beginDate;
  private LocalDate endDate;
  private EastMoneyQTKlineTypeEnum klt;
  private EastMoneyQTKlineWeightingEnum fqt;
}
