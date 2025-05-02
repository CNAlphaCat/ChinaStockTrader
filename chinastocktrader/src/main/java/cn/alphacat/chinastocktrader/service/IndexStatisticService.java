package cn.alphacat.chinastocktrader.service;

import cn.alphacat.chinastockdata.model.IndexPE;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastocktrader.entity.IndexPEEntity;
import cn.alphacat.chinastocktrader.model.CSI1000DivideCSI300;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class IndexStatisticService {
  private final CSI300IndexService csi300IndexService;
  private final CSI1000IndexService csi1000IndexService;

  public IndexStatisticService(
      final CSI300IndexService csi300IndexService, final CSI1000IndexService csi1000IndexService) {
    this.csi300IndexService = csi300IndexService;
    this.csi1000IndexService = csi1000IndexService;
  }

  public Map<LocalDate, IndexPE> getCSI300IndexPE(LocalDate startDate) {
    return csi300IndexService.getCSI300IndexPE(startDate);
  }

  public List<CSI1000DivideCSI300> getCSI1000DivideCSI300(LocalDate startDate) {
    List<MarketIndex> csi300IndexDaily = getCSI300IndexDaily(startDate);
    List<MarketIndex> csi1000IndexDaily = getCSI1000IndexDaily(startDate);

    if (csi300IndexDaily.size() != csi1000IndexDaily.size()) {
      return null;
    }

    List<CSI1000DivideCSI300> resultList = new ArrayList<>();
    for (int i = 0; i < csi300IndexDaily.size(); i++) {
      MarketIndex csi300Index = csi300IndexDaily.get(i);
      MarketIndex csi1000Index = csi1000IndexDaily.get(i);
      if (csi300Index.getTradeDate().isBefore(startDate)) {
        continue;
      }
      CSI1000DivideCSI300 CSI1000DivideCSI300BO = new CSI1000DivideCSI300();
      CSI1000DivideCSI300BO.setDate(csi300Index.getTradeDate());
      BigDecimal result =
          csi1000Index.getClose().divide(csi300Index.getClose(), 6, RoundingMode.HALF_UP);
      CSI1000DivideCSI300BO.setDividedValue(result);

      resultList.add(CSI1000DivideCSI300BO);
    }
    return resultList;
  }

  private List<MarketIndex> getCSI300IndexDaily(LocalDate startDate) {
    List<MarketIndex> csi300IndexDaily = csi300IndexService.getCSI300IndexDaily(startDate);
    csi300IndexDaily.sort(Comparator.comparing(MarketIndex::getTradeDate));
    return csi300IndexDaily;
  }

  private List<MarketIndex> getCSI1000IndexDaily(LocalDate startDate) {
    List<MarketIndex> csi1000IndexDaily = csi1000IndexService.getCSI1000IndexDaily(startDate);
    csi1000IndexDaily.sort(Comparator.comparing(MarketIndex::getTradeDate));
    return csi1000IndexDaily;
  }
}
