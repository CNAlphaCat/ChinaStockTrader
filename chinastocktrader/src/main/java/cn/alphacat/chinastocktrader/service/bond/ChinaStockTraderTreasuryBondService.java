package cn.alphacat.chinastocktrader.service.bond;

import cn.alphacat.chinastockdata.bond.TreasuryBondService;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastocktrader.entity.TreasuryBondEntity;
import cn.alphacat.chinastocktrader.repository.TreasuryBondRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChinaStockTraderTreasuryBondService {
  private final TreasuryBondService treasuryBondService;
  private final TreasuryBondRepository treasuryBondRepository;

  private final Executor taskExecutor;

  public ChinaStockTraderTreasuryBondService(
      final TreasuryBondService treasuryBondService,
      final TreasuryBondRepository treasuryBondRepository,
      final Executor taskExecutor) {
    this.treasuryBondService = treasuryBondService;
    this.treasuryBondRepository = treasuryBondRepository;
    this.taskExecutor = taskExecutor;
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
    LocalDate latestTradeDateValueInDB =
        treasuryBondRepository.findMaxSolarDate().orElse(earliestSolarDateValueInDB);

    CompletableFuture.runAsync(
            () ->
                getDataFromAPIAndSaveToDB(
                    startDate, latestTradeDateValueInDB, earliestSolarDateValueInDB),
            taskExecutor)
        .exceptionally(
            ex -> {
              log.error("Failed from API getSortedTreasuryBondDataList: {}", ex.getMessage());
              return null;
            });

    List<TreasuryBondEntity> bySolarDateIsGreaterThanEqual =
        treasuryBondRepository.findBySolarDateIsGreaterThanEqual(startDate);
    return bySolarDateIsGreaterThanEqual.stream()
        .map(EntityConverter::convertToModel)
        .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
        .toList();
  }

  private void getDataFromAPIAndSaveToDB(
      LocalDate startDate,
      LocalDate latestTradeDateValueInDB,
      LocalDate earliestSolarDateValueInDB) {
    Map<LocalDate, TreasuryBond> tenYearTreasuryBond =
        treasuryBondService.getTreasuryBondDataMap(startDate);
    List<TreasuryBondEntity> treasuryBondEntities =
        tenYearTreasuryBond.values().stream()
            .filter(
                treasuryBond -> {
                  LocalDate solarDate = treasuryBond.getSolarDate();
                  if (solarDate == null) {
                    return false;
                  }
                  if (solarDate.isEqual(LocalDateUtil.getNow())) {
                    return false;
                  }
                  if (solarDate.isAfter(latestTradeDateValueInDB)) {
                    return true;
                  }
                  return solarDate.isBefore(earliestSolarDateValueInDB);
                })
            .map(EntityConverter::convertToEntity)
            .collect(Collectors.toCollection(() -> new ArrayList<>(tenYearTreasuryBond.size())));
    treasuryBondRepository.saveAll(treasuryBondEntities);
  }
}
