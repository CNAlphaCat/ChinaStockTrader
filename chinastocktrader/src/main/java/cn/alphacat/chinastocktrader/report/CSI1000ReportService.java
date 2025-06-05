package cn.alphacat.chinastocktrader.report;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastocktrader.service.future.IMFutureService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CSI1000ReportService {
  private final IMFutureService imFutureService;

  public CSI1000ReportService(final IMFutureService imFutureService) {
    this.imFutureService = imFutureService;
  }

  public List<IMVolatilityBO> getCSI1000VolatilityReport() {
    LocalDate beginDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.now();
    return imFutureService
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
        .sorted(Comparator.comparing(IMVolatilityBO::getHighLowRange))
        .toList();
  }
}
