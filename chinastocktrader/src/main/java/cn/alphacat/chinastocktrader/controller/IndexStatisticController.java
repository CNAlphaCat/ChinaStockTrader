package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.model.marketindex.IndexPE;
import cn.alphacat.chinastocktrader.model.CSI1000DivideCSI300;
import cn.alphacat.chinastocktrader.model.EquityPremiumIndex;
import cn.alphacat.chinastocktrader.service.marketindex.IndexStatisticService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/index/statistic")
public class IndexStatisticController {
  private final IndexStatisticService indexStatisticService;

  public IndexStatisticController(final IndexStatisticService indexStatisticService) {
    this.indexStatisticService = indexStatisticService;
  }

  @GetMapping("/CSI1000DivideCSI300/{startDate}")
  public List<CSI1000DivideCSI300> getCSI300DivideCSI1000(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return indexStatisticService.getSortedCSI1000DivideCSI300(startDate);
  }

  @GetMapping("/CSI300indexPE/{startDate}")
  public Map<LocalDate, IndexPE> getCSI300IndexPE(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return indexStatisticService.getSortedCSI300IndexPEMap(startDate);
  }

  @GetMapping("/equitypremiumindex/{startDate}")
  public List<EquityPremiumIndex> getSortedEquityPremiumIndex(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return indexStatisticService.getSortedEquityPremiumIndexWithPercentile(startDate);
  }
}
