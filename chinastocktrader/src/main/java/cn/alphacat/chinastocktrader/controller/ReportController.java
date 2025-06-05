package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastocktrader.report.IMReportService;
import cn.alphacat.chinastocktrader.report.IMVolatilityBO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
  private final IMReportService imReportService;

  public ReportController(final IMReportService imReportService) {
    this.imReportService = imReportService;
  }

  @RequestMapping("/IMVolatilityReport/{startDate}")
  public List<IMVolatilityBO> getIMReport(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return imReportService.getIMVolatilityReport(startDate);
  }
}
