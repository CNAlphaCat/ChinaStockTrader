package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastocktrader.model.CSI1000DivideCSI300;
import cn.alphacat.chinastocktrader.service.IndexStatisticService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/index/statistic")
public class IndexStatisticController {
  private final IndexStatisticService indexStatisticService;

  public IndexStatisticController(final IndexStatisticService indexStatisticService) {
    this.indexStatisticService = indexStatisticService;
  }

  @RequestMapping("/CSI1000DivideCSI300/{startDate}")
  public List<CSI1000DivideCSI300> getCSI300DivideCSI1000(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return indexStatisticService.getCSI1000DivideCSI300(startDate);
  }
}
