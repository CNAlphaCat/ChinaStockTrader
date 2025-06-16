package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import cn.alphacat.chinastocktrader.service.future.ChinaStockTraderFutureService;
import cn.alphacat.chinastocktrader.view.FutureHistoryView;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/future")
public class FutureController {
  private final ChinaStockTraderFutureService chinaStockTraderFutureService;

  public FutureController(final ChinaStockTraderFutureService chinaStockTraderFutureService) {
    this.chinaStockTraderFutureService = chinaStockTraderFutureService;
  }

  @RequestMapping("/getIFFutureHistory")
  public List<FutureHistory> getIFFutureHistory(@RequestBody FutureHistoryView view) {
    return chinaStockTraderFutureService.getIFFutureHistory(
        view.getBeginDate(), view.getEndDate(), view.getKlt(), view.getFqt());
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
