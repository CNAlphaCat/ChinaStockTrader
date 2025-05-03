package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.FutureHistory;
import cn.alphacat.chinastockdata.model.FutureMarketOverview;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ChinaStockTraderFutureService {
  private final FutureService featureService;
  private final IFFutureService ifFutureService;
  private final IMFutureService imFutureService;

  public ChinaStockTraderFutureService(
      final FutureService featureService,
      final IFFutureService ifFutureService,
      final IMFutureService imFutureService) {
    this.featureService = featureService;
    this.ifFutureService = ifFutureService;
    this.imFutureService = imFutureService;
  }

  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return featureService.getFuturesBaseInfo();
  }

  public BigDecimal getCurrentIMDivideIFPrice() {
    List<FutureHistory> imHistoryList = imFutureService.getTodayFutureHistory();
    List<FutureHistory> ifHistoryList = ifFutureService.getTodayFutureHistory();
    FutureHistory imHistory = imHistoryList.getLast();
    FutureHistory ifHistory = ifHistoryList.getLast();
    BigDecimal imPrice = imHistory.getClose();
    BigDecimal ifPrice = ifHistory.getClose();
    return imPrice.divide(ifPrice, 4, RoundingMode.HALF_UP);
  }
}
