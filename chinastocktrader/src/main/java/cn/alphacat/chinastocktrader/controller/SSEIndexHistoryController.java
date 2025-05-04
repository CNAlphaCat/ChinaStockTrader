package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastocktrader.model.OnePercentVolatilityFunds;
import cn.alphacat.chinastocktrader.service.marketindex.SSEIndexHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sse")
public class SSEIndexHistoryController {
  private final SSEIndexHistoryService sseIndexHistoryService;

  public SSEIndexHistoryController(final SSEIndexHistoryService sseIndexHistoryService) {
    this.sseIndexHistoryService = sseIndexHistoryService;
  }

  @GetMapping("/onePercentVolatilityFunds/{startDate}")
  public List<OnePercentVolatilityFunds> getShanghaiIndexHistory(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return sseIndexHistoryService.getSortedOnePercentVolatilityFunds(startDate);
  }
}
