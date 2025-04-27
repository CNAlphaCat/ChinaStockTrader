package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.feature.FeatureService;
import cn.alphacat.chinastockdata.model.FeatureMarketOverview;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feature")
public class FeatureController {
  private final FeatureService featureService;

  public FeatureController(final FeatureService featureService) {
    this.featureService = featureService;
  }

  @RequestMapping("/futuresBaseInfo")
  public List<FeatureMarketOverview> getFuturesBaseInfo() {
    return featureService.getFuturesBaseInfo();
  }
}
