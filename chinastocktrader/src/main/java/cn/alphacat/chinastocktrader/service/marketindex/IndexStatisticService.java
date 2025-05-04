package cn.alphacat.chinastocktrader.service.marketindex;

import cn.alphacat.chinastockdata.model.IndexPE;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastocktrader.model.CSI1000DivideCSI300;
import cn.alphacat.chinastocktrader.model.EquityPremiumIndex;
import cn.alphacat.chinastocktrader.service.bond.ChinaStockTraderTreasuryBondService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexStatisticService {
  private final CSI300IndexService csi300IndexService;
  private final CSI1000IndexService csi1000IndexService;
  private final ChinaStockTraderTreasuryBondService chinaStockTraderTreasuryBondService;

  public IndexStatisticService(
      final CSI300IndexService csi300IndexService,
      final CSI1000IndexService csi1000IndexService,
      final ChinaStockTraderTreasuryBondService chinaStockTraderTreasuryBondService) {
    this.csi300IndexService = csi300IndexService;
    this.csi1000IndexService = csi1000IndexService;
    this.chinaStockTraderTreasuryBondService = chinaStockTraderTreasuryBondService;
  }

  public List<EquityPremiumIndex> getEquityPremiumIndex(LocalDate startDate) {
    Map<LocalDate, IndexPE> csi300IndexPE = getSortedCSI300IndexPEMap(startDate);
    Map<LocalDate, TreasuryBond> treasuryBondMap =
        chinaStockTraderTreasuryBondService.getSortedTreasuryBondDataMap(startDate);

    List<LocalDate> commonDates = LocalDateUtil.findCommonDates(csi300IndexPE, treasuryBondMap);
    List<EquityPremiumIndex> result = new ArrayList<>();

    for (LocalDate date : commonDates) {
      IndexPE indexPE = csi300IndexPE.get(date);
      TreasuryBond treasuryBond = treasuryBondMap.get(date);
      EquityPremiumIndex equityPremiumIndex = EquityPremiumIndex.build(indexPE, treasuryBond);
      if (equityPremiumIndex == null) {
        continue;
      }
      result.add(equityPremiumIndex);
    }
    return result;
  }

  public List<EquityPremiumIndex> getSortedEquityPremiumIndexWithPercentile(LocalDate startDate) {
    List<EquityPremiumIndex> result = getEquityPremiumIndex(startDate);

    List<BigDecimal> indexValues =
        result.stream()
            .map(EquityPremiumIndex::getEquityPremiumIndex)
            .filter(Objects::nonNull)
            .sorted()
            .toList();

    int n = indexValues.size();
    if (n == 0) {
      return result;
    }

    Map<BigDecimal, Double> percentileMap = new HashMap<>();
    for (int i = 0; i < n; i++) {
      BigDecimal value = indexValues.get(i);
      double percentile = (double) i / (n - 1) * 100;
      percentileMap.put(value, percentile);
    }
    for (EquityPremiumIndex e : result) {
      if (e.getEquityPremiumIndex() != null
          && percentileMap.containsKey(e.getEquityPremiumIndex())) {
        e.setPercentile(
            BigDecimal.valueOf(percentileMap.get(e.getEquityPremiumIndex()))
                .setScale(2, RoundingMode.HALF_UP));
      }
    }
    return result.stream().sorted(Comparator.comparing(EquityPremiumIndex::getDate)).toList();
  }

  public LinkedHashMap<LocalDate, IndexPE> getSortedCSI300IndexPEMap(LocalDate startDate) {
    Map<LocalDate, IndexPE> result = csi300IndexService.getCSI300IndexPE(startDate);
    return result.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
  }

  public List<CSI1000DivideCSI300> getSortedCSI1000DivideCSI300(LocalDate startDate) {
    LinkedHashMap<LocalDate, MarketIndex> csi300IndexDaily = getCSI300IndexDaily(startDate);
    LinkedHashMap<LocalDate, MarketIndex> csi1000IndexDaily = getCSI1000IndexDaily(startDate);

    List<LocalDate> commonDates =
        LocalDateUtil.findCommonDates(csi300IndexDaily, csi1000IndexDaily);

    List<CSI1000DivideCSI300> resultList = new ArrayList<>();
    for (LocalDate date : commonDates) {
      MarketIndex csi300Index = csi300IndexDaily.get(date);
      MarketIndex csi1000Index = csi1000IndexDaily.get(date);

      CSI1000DivideCSI300 CSI1000DivideCSI300BO = new CSI1000DivideCSI300();
      CSI1000DivideCSI300BO.setDate(csi300Index.getTradeDate());
      BigDecimal result =
          csi1000Index.getClose().divide(csi300Index.getClose(), 6, RoundingMode.HALF_UP);
      CSI1000DivideCSI300BO.setDividedValue(result);

      resultList.add(CSI1000DivideCSI300BO);
    }
    return resultList.stream().sorted(Comparator.comparing(CSI1000DivideCSI300::getDate)).toList();
  }

  private LinkedHashMap<LocalDate, MarketIndex> getCSI300IndexDaily(LocalDate startDate) {
    return csi300IndexService.getCSI300IndexDaily(startDate).stream()
        .sorted(Comparator.comparing(MarketIndex::getTradeDate))
        .collect(
            Collectors.toMap(
                MarketIndex::getTradeDate,
                mi -> mi,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  private LinkedHashMap<LocalDate, MarketIndex> getCSI1000IndexDaily(LocalDate startDate) {
    return csi1000IndexService.getCSI1000IndexDaily(startDate).stream()
        .sorted(Comparator.comparing(MarketIndex::getTradeDate))
        .collect(
            Collectors.toMap(
                MarketIndex::getTradeDate,
                mi -> mi,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }
}
