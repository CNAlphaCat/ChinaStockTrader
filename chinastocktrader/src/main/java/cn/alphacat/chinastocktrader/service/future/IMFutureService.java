package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IMFutureService {
  private final FutureService featureService;

  private static final String IM_MAIN_FUTURE_CODE = "8.150130";

  public IMFutureService(final FutureService featureService) {
    this.featureService = featureService;
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
}
