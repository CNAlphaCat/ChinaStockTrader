package cn.alphacat.chinastocktrader.model.report;

import lombok.Data;

import java.util.Map;

@Data
public class CommonReport {
  private String reportName;
  private Map<String, String> reportData;
}
