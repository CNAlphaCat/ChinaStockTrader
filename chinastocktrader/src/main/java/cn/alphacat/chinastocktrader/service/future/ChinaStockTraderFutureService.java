package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.CFFEXFutureHistoryPrefixEnums;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
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

  public List<FutureHistory> getIFFutureHistory(
      LocalDate beginDate,
      LocalDate endDate,
      EastMoneyQTKlineTypeEnum klt,
      EastMoneyQTKlineWeightingEnum fqt) {
    return ifFutureService.getFutureHistory(beginDate, endDate, klt, fqt);
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

  public HashMap<LocalDate, List<CFFEXFutureHistory>> getFutureHistory(Year year, Month month) {
    List<CFFEXFutureHistoryPrefixEnums> prefixEnums =
        List.of(
            CFFEXFutureHistoryPrefixEnums.IF,
            CFFEXFutureHistoryPrefixEnums.IM,
            CFFEXFutureHistoryPrefixEnums.IC);
    return featureService.getFutureHistory(year, month, prefixEnums);
  }
}
