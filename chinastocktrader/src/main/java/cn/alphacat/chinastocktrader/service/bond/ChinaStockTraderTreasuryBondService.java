package cn.alphacat.chinastocktrader.service.bond;

import cn.alphacat.chinastockdata.bond.TreasuryBondService;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastocktrader.entity.TreasuryBondEntity;
import cn.alphacat.chinastocktrader.repository.TreasuryBondRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChinaStockTraderTreasuryBondService {
  private final TreasuryBondService treasuryBondService;
  private final TreasuryBondRepository treasuryBondRepository;

  public ChinaStockTraderTreasuryBondService(
      final TreasuryBondService treasuryBondService,
      final TreasuryBondRepository treasuryBondRepository) {
    this.treasuryBondService = treasuryBondService;
    this.treasuryBondRepository = treasuryBondRepository;
  }

  public LinkedHashMap<LocalDate, TreasuryBond> getSortedTreasuryBondDataMap(LocalDate startDate) {
    List<TreasuryBond> treasuryBondDataList = getSortedTreasuryBondDataList(startDate);
    return treasuryBondDataList.stream()
        .collect(
            Collectors.toMap(
                TreasuryBond::getSolarDate,
                treasuryBond -> treasuryBond,
                (existingValue, newValue) -> existingValue,
                LinkedHashMap::new));
  }

  public List<TreasuryBond> getSortedTreasuryBondDataList(LocalDate startDate) {
    Optional<LocalDate> earliestSolarDateInDB = treasuryBondRepository.findMinSolarDate();
    if (earliestSolarDateInDB.isEmpty()) {
      Map<LocalDate, TreasuryBond> tenYearTreasuryBond =
          treasuryBondService.getTreasuryBondDataMap(startDate);
      List<TreasuryBondEntity> treasuryBondEntities =
          tenYearTreasuryBond.values().stream()
              .map(EntityConverter::convertToEntity)
              .collect(Collectors.toCollection(() -> new ArrayList<>(tenYearTreasuryBond.size())));
      treasuryBondRepository.saveAll(treasuryBondEntities);
      return tenYearTreasuryBond.values().stream()
          .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
          .toList();
    }
    LocalDate earliestSolarDateValueInDB = earliestSolarDateInDB.get();

    if (startDate.isAfter(earliestSolarDateValueInDB)
        || startDate.isEqual(earliestSolarDateValueInDB)) {
      List<TreasuryBondEntity> bySolarDateIsGreaterThanEqual =
          treasuryBondRepository.findBySolarDateIsGreaterThanEqual(startDate);
      return bySolarDateIsGreaterThanEqual.stream()
          .map(EntityConverter::convertToModel)
          .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
          .toList();
    }

    Map<LocalDate, TreasuryBond> tenYearTreasuryBond =
        treasuryBondService.getTreasuryBondDataMap(startDate);
    List<TreasuryBondEntity> treasuryBondEntities =
        tenYearTreasuryBond.values().stream()
            .filter(
                treasuryBond -> {
                  LocalDate solarDate = treasuryBond.getSolarDate();
                  return solarDate != null && solarDate.isBefore(earliestSolarDateValueInDB);
                })
            .map(EntityConverter::convertToEntity)
            .collect(Collectors.toCollection(() -> new ArrayList<>(tenYearTreasuryBond.size())));
    treasuryBondRepository.saveAll(treasuryBondEntities);
    return tenYearTreasuryBond.values().stream()
        .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
        .toList();
  }
}
