package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.service.marketindex.CSI300IndexService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.future.DiffBetweenIFAndIndexView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IFFutureService {
  private final FutureService featureService;
  private final CSI300IndexService csi300IndexService;

  private static final String IF_MAIN_FUTURE_CODE = "8.040130";

  public IFFutureService(
      final FutureService featureService, final CSI300IndexService csi300IndexService) {
    this.featureService = featureService;
    this.csi300IndexService = csi300IndexService;
  }

  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return featureService.getFuturesBaseInfo();
  }

  public List<FutureHistory> getFutureHistory(
      LocalDate beginDate,
      LocalDate endDate,
      EastMoneyQTKlineTypeEnum klt,
      EastMoneyQTKlineWeightingEnum fqt) {
    return featureService.getFutureHistory(IF_MAIN_FUTURE_CODE, beginDate, endDate, klt, fqt);
  }

  public List<FutureHistory> getTodayFutureHistory() {
    return featureService.getFutureHistory(
        IF_MAIN_FUTURE_CODE,
        LocalDateUtil.getNow(),
        LocalDateUtil.getNow(),
        EastMoneyQTKlineTypeEnum.ONE_MINUTE,
        EastMoneyQTKlineWeightingEnum.NON_WEIGHTING);
  }

  public List<DiffBetweenIFAndIndexView> getDiffBetweenIFAndIndex(int startYear, Month startMonth) {
    List<FutureHistory> noSmoothIMFutureHistory = getNoSmoothIFFutureHistory(startYear, startMonth);
    Map<LocalDate, FutureHistory> noSmoothIMFutureHistoryMap =
        noSmoothIMFutureHistory.stream()
            .collect(Collectors.toMap(FutureHistory::getDate, futureHistory -> futureHistory));
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    List<MarketIndex> csi300IndexDaily = csi300IndexService.getCSI300IndexDaily(startDate);
    Map<LocalDate, MarketIndex> csi300IndexDailyMap =
        csi300IndexDaily.stream()
            .collect(Collectors.toMap(MarketIndex::getTradeDate, marketIndex -> marketIndex));
    List<DiffBetweenIFAndIndexView> result = new ArrayList<>();
    for (LocalDate date : csi300IndexDailyMap.keySet()) {
      FutureHistory futureHistory = noSmoothIMFutureHistoryMap.get(date);
      if (futureHistory == null) {
        continue;
      }
      MarketIndex csi1000Index = csi300IndexDailyMap.get(date);
      if (csi1000Index == null) {
        continue;
      }
      DiffBetweenIFAndIndexView diffBetweenIFAndIndexView = new DiffBetweenIFAndIndexView();
      diffBetweenIFAndIndexView.setDate(date);
      diffBetweenIFAndIndexView.setDiff(futureHistory.getClose().subtract(csi1000Index.getClose()));
      result.add(diffBetweenIFAndIndexView);
    }
    return result.stream()
        .sorted(Comparator.comparing(DiffBetweenIFAndIndexView::getDate))
        .toList();
  }

  public List<FutureHistory> getNoSmoothIFFutureHistory(int startYear, Month startMonth) {
    HashMap<LocalDate, List<CFFEXFutureHistory>> stockFutureHistory =
        featureService.getStockFutureHistory(startYear, startMonth);
    List<FutureHistory> result = new ArrayList<>();
    for (List<CFFEXFutureHistory> futureHistoryList : stockFutureHistory.values()) {
      CFFEXFutureHistory mainIFHistory = getMainIFHistory(futureHistoryList);
      if (mainIFHistory == null) {
        continue;
      }
      FutureHistory futureHistory = new FutureHistory(mainIFHistory);
      result.add(futureHistory);
    }
    return result.stream().sorted(Comparator.comparing(FutureHistory::getDate)).toList();
  }

  private CFFEXFutureHistory getMainIFHistory(List<CFFEXFutureHistory> cffexFutureHistoryList) {
    if (cffexFutureHistoryList.isEmpty()) {
      return null;
    }
    CFFEXFutureHistory result = null;
    BigDecimal holdingVolume = BigDecimal.ZERO;
    BigDecimal amount = BigDecimal.ZERO;
    for (CFFEXFutureHistory cffexFutureHistory : cffexFutureHistoryList) {
      if (!cffexFutureHistory.getCode().startsWith("IF")) {
        continue;
      }
      if (cffexFutureHistory.getAmount().compareTo(amount) > 0
          && cffexFutureHistory.getHoldingVolume().compareTo(holdingVolume) > 0) {
        result = cffexFutureHistory;
        amount = cffexFutureHistory.getAmount();
        holdingVolume = cffexFutureHistory.getHoldingVolume();
      }
    }
    return result;
  }
}
