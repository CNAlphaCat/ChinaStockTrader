package cn.alphacat.chinastocktrader.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalDateUtil {

  public static List<LocalDate> findCommonDates(Map<LocalDate, ?> map1, Map<LocalDate, ?> map2) {
    return map1.keySet().stream().filter(map2::containsKey).sorted().collect(Collectors.toList());
  }
}
