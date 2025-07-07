package cn.alphacat.chinastocktrader.model.future;

import cn.alphacat.chinastockdata.model.future.FutureHistory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IFHistory {
  private FutureHistory main;
  private FutureHistory recentlyMonth;
  private LocalDate date;
}
