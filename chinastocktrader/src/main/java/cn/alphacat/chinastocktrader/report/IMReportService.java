package cn.alphacat.chinastocktrader.report;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastocktrader.service.future.IMFutureService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IMReportService {
  private final IMFutureService imFutureService;

  public IMReportService(final IMFutureService imFutureService) {
    this.imFutureService = imFutureService;
  }

  public List<IMVolatilityBO> getIMVolatilityReport(LocalDate beginDate) {
    LocalDate endDate = LocalDate.now();
    List<IMVolatilityBO> list =
        imFutureService
            .getFutureHistory(
                beginDate,
                endDate,
                EastMoneyQTKlineTypeEnum.DAILY,
                EastMoneyQTKlineWeightingEnum.PRE_WEIGHTING)
            .stream()
            .map(
                future -> {
                  IMVolatilityBO bo = new IMVolatilityBO();
                  bo.setDate(future.getDate());
                  BigDecimal highLowRange = future.getHigh().subtract(future.getLow());
                  bo.setHighLowRange(highLowRange);
                  return bo;
                })
            .sorted(Comparator.comparing(IMVolatilityBO::getDate))
            .toList();
    List<BigDecimal> allRanges =
        list.stream().map(IMVolatilityBO::getHighLowRange).sorted().toList();
    for (IMVolatilityBO bo : list) {
      BigDecimal currentRange = bo.getHighLowRange();
      long lowerCount = allRanges.stream().filter(r -> r.compareTo(currentRange) < 0).count();
      long equalCount = allRanges.stream().filter(r -> r.compareTo(currentRange) == 0).count();

      int total = allRanges.size();
      BigDecimal percentile =
          BigDecimal.valueOf((lowerCount + 0.5 * equalCount) / total * 100)
              .setScale(2, RoundingMode.HALF_UP);

      bo.setPercentile(percentile);
    }
    return list;
  }
}
