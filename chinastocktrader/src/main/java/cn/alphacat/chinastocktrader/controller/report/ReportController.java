package cn.alphacat.chinastocktrader.controller.report;

import cn.alphacat.chinastocktrader.model.report.CommonReport;
import cn.alphacat.chinastocktrader.report.IMVolatilityReportHandler;
import cn.alphacat.chinastocktrader.model.report.IMVolatilityBO;
import cn.alphacat.chinastocktrader.service.report.CommonReportEnums;
import cn.alphacat.chinastocktrader.service.report.CommonReportFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
  private final CommonReportFactory commonReportFactory;
  private final IMVolatilityReportHandler imVolatilityReportHandler;

  public ReportController(
      CommonReportFactory commonReportFactory, final IMVolatilityReportHandler imVolatilityReportHandler) {
    this.commonReportFactory = commonReportFactory;
    this.imVolatilityReportHandler = imVolatilityReportHandler;
  }

  @RequestMapping("/list/reportType")
  public List<String> getReportTypeList() {
    return CommonReportEnums.getReportTypeList();
  }

  @RequestMapping("/common/{reportType}")
  public CommonReport getStockOpenPriceAndRiseReport(@PathVariable String reportType) {
    return commonReportFactory.generateCommonReport(CommonReportEnums.valueOf(reportType));
  }

  @RequestMapping("/IMVolatilityReport/{startDate}")
  public List<IMVolatilityBO> getIMReport(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return imVolatilityReportHandler.getIMVolatilityReport(startDate);
  }
}
