package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.service.marketindex.CSI1000IndexService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.future.DiffBetweenIMAndIndexView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IMFutureService {
  private final FutureService featureService;
  private final CSI1000IndexService csi1000IndexService;

  private static final String IM_MAIN_FUTURE_CODE = "8.150130";

  public IMFutureService(
      final FutureService featureService, final CSI1000IndexService csi1000IndexService) {
    this.featureService = featureService;
    this.csi1000IndexService = csi1000IndexService;
  }

  public List<FutureHistory> getFutureHistory(
      LocalDate beginDate,
      LocalDate endDate,
      EastMoneyQTKlineTypeEnum klt,
      EastMoneyQTKlineWeightingEnum fqt) {
    return featureService.getFutureHistory(IM_MAIN_FUTURE_CODE, beginDate, endDate, klt, fqt);
  }

  public List<FutureHistory> getTodayFutureHistory() {
    return featureService.getFutureHistory(
        IM_MAIN_FUTURE_CODE,
        LocalDateUtil.getNow(),
        LocalDateUtil.getNow(),
        EastMoneyQTKlineTypeEnum.ONE_MINUTE,
        EastMoneyQTKlineWeightingEnum.NON_WEIGHTING);
  }

  public List<DiffBetweenIMAndIndexView> getDiffBetweenIMAndIndex(int startYear, Month startMonth) {
    List<FutureHistory> noSmoothIMFutureHistory = getNoSmoothIMFutureHistory(startYear, startMonth);
    Map<LocalDate, FutureHistory> noSmoothIMFutureHistoryMap =
        noSmoothIMFutureHistory.stream()
            .collect(Collectors.toMap(FutureHistory::getDate, futureHistory -> futureHistory));
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    List<MarketIndex> csi1000IndexDaily = csi1000IndexService.getCSI1000IndexDaily(startDate);
    Map<LocalDate, MarketIndex> csi1000IndexDailyMap =
        csi1000IndexDaily.stream()
            .collect(Collectors.toMap(MarketIndex::getTradeDate, marketIndex -> marketIndex));
    List<DiffBetweenIMAndIndexView> result = new ArrayList<>();
    for (LocalDate date : csi1000IndexDailyMap.keySet()) {
      FutureHistory futureHistory = noSmoothIMFutureHistoryMap.get(date);
      if (futureHistory == null) {
        continue;
      }
      MarketIndex csi1000Index = csi1000IndexDailyMap.get(date);
      if (csi1000Index == null) {
        continue;
      }
      DiffBetweenIMAndIndexView diffBetweenIMAndIndexView = new DiffBetweenIMAndIndexView();
      diffBetweenIMAndIndexView.setDate(date);
      diffBetweenIMAndIndexView.setDiff(futureHistory.getClose().subtract(csi1000Index.getClose()));
      result.add(diffBetweenIMAndIndexView);
    }
    return result.stream()
        .sorted(Comparator.comparing(DiffBetweenIMAndIndexView::getDate))
        .toList();
  }

  public List<FutureHistory> getNoSmoothIMFutureHistory(int startYear, Month startMonth) {
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
      if (!cffexFutureHistory.getCode().startsWith("IM")) {
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
