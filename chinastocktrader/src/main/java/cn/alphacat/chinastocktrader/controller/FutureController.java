package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.FutureMarketOverview;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feature")
public class FutureController {
  private final FutureService futureService;

  public FutureController(final FutureService futureService) {
    this.futureService = futureService;
  }

  @RequestMapping("/futuresBaseInfo")
  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return futureService.getFuturesBaseInfo();
  }


}
