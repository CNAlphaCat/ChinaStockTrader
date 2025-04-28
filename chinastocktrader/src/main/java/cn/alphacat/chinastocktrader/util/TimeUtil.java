package cn.alphacat.chinastocktrader.util;

import java.time.LocalTime;
import java.time.ZoneId;

public class TimeUtil {

  public static boolean isAfterStockCloseTime() {
    LocalTime now = LocalTime.now(ZoneId.of("Asia/Shanghai"));
    LocalTime closeTime = LocalTime.of(15, 0);
    return now.isAfter(closeTime);
  }

  public static boolean isBeforeStockOpenTime() {
    LocalTime now = LocalTime.now(ZoneId.of("Asia/Shanghai"));
    LocalTime openTime = LocalTime.of(9, 15);
    return now.isBefore(openTime);
  }
}
