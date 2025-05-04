package cn.alphacat.chinastocktrader.controller.marketindex;

import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.service.marketindex.SZCIHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/szci")
public class SZCIIndexHistoryController {
  private final SZCIHistoryService szciHistoryService;

  public SZCIIndexHistoryController(final SZCIHistoryService szciHistoryService) {
    this.szciHistoryService = szciHistoryService;
  }

  @GetMapping("/shenzhenIndexHistory/{startDate}")
  public List<MarketIndex> getShenzhenIndexHistory(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return szciHistoryService.getSortedShenzhenIndexHistory(startDate);
  }
}
