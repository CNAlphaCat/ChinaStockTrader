package cn.alphacat.chinastocktrader.util;

import java.time.LocalTime;
import java.time.ZoneId;

public class LocalDateTimeUtil {
  public static boolean isAfterStockCloseTime() {
    LocalTime now = LocalTime.now(ZoneId.of("Asia/Shanghai"));
    LocalTime openTime = LocalTime.of(15, 0);
    return now.isAfter(openTime);
  }

  public static boolean isBeforeEqualStockCloseTime() {
    return !isAfterStockCloseTime();
  }
}
