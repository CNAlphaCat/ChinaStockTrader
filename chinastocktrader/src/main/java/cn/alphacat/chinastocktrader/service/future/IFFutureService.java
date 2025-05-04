package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IFFutureService {
  private final FutureService featureService;

  private static final String IF_MAIN_FUTURE_CODE = "8.040130";

  public IFFutureService(final FutureService featureService) {
    this.featureService = featureService;
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
        LocalDate.now(),
        LocalDate.now(),
        EastMoneyQTKlineTypeEnum.ONE_MINUTE,
        EastMoneyQTKlineWeightingEnum.NON_WEIGHTING);
  }
}
