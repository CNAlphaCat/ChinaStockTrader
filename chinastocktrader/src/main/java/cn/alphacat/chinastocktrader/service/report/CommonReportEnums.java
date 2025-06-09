package cn.alphacat.chinastocktrader.service.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CommonReportEnums {
  STOCK_OPEN_PRICE_AND_RISE("stockOpenPriceAndRise");

  private final String reportType;

  public static CommonReportEnums getByReportType(String reportType) {
    for (CommonReportEnums value : values()) {
      if (value.reportType.equals(reportType)) {
        return value;
      }
    }
    return null;
  }

  public static List<String> getReportTypeList() {
    List<String> reportTypeList = new ArrayList<>();
    for (CommonReportEnums value : values()) {
      reportTypeList.add(value.reportType);
    }
    return reportTypeList;
  }
}
