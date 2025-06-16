package cn.alphacat.chinastocktrader.controller.market;

import cn.alphacat.chinastockdata.model.stock.StockLimitUpSummary;
import cn.alphacat.chinastocktrader.service.market.MarketStatisticService;
import cn.alphacat.chinastocktrader.view.stock.StockLimitView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market/statistic")
public class MarketStatisticController {
  private final MarketStatisticService marketStatisticService;

  public MarketStatisticController(final MarketStatisticService marketStatisticService) {
    this.marketStatisticService = marketStatisticService;
  }

  @GetMapping("/stockLimitSummary")
  public List<StockLimitView> getStockLimitSummary() {
    return marketStatisticService.getSortedStockLimitView();
  }


  @GetMapping("/stockLimitUpSummary")
  public List<StockLimitUpSummary> getStockLimitUpSummary() {
    Map<LocalDate, StockLimitUpSummary> stockLimitUpMap =
        marketStatisticService.getStockLimitUpSummary();
    return stockLimitUpMap.values().stream()
        .sorted(Comparator.comparing(StockLimitUpSummary::getTradeDate))
        .toList();
  }
}
