package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.model.FutureMarketOverview;
import cn.alphacat.chinastocktrader.service.ChinaStockTraderFutureService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/future")
public class FutureController {
  private final ChinaStockTraderFutureService chinaStockTraderFutureService;

  public FutureController(final ChinaStockTraderFutureService chinaStockTraderFutureService) {
    this.chinaStockTraderFutureService = chinaStockTraderFutureService;
  }

  /*
   * 获取所有期货品种信息
   */
  @RequestMapping("/futuresBaseInfo")
  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return chinaStockTraderFutureService.getFuturesBaseInfo();
  }

  /*
   * 获取IM和IF的比值
   */
  @RequestMapping("/currentIMDivideIFPrice")
  public BigDecimal getCurrentIMDivideIFPrice() {
    return chinaStockTraderFutureService.getCurrentIMDivideIFPrice();
  }
}
