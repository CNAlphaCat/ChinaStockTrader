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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChinaStockTraderTreasuryBondService {
  private final TreasuryBondService treasuryBondService;
  private final TreasuryBondRepository treasuryBondRepository;

  private final Executor taskExecutor;

  private final ReentrantLock lock = new ReentrantLock();

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
    if (treasuryBondRepository.count() == 0) {
      return initData(startDate);
    }
    CompletableFuture.runAsync(() -> getDataFromAPIAndSaveToDB(startDate), taskExecutor)
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

  private List<TreasuryBond> initData(LocalDate startDate) {
    lock.lock();
    try {
      if (treasuryBondRepository.count() > 0) {
        return treasuryBondRepository.findBySolarDateIsGreaterThanEqual(startDate).stream()
            .map(EntityConverter::convertToModel)
            .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
            .toList();
      }
      Map<LocalDate, TreasuryBond> tenYearTreasuryBond =
          treasuryBondService.getTreasuryBondDataMap(startDate);
      List<TreasuryBondEntity> treasuryBondEntities =
          tenYearTreasuryBond.values().stream()
              .map(EntityConverter::convertToEntity)
              .filter(
                  treasuryBond -> {
                    LocalDate solarDate = treasuryBond.getSolarDate();
                    if (solarDate == null) {
                      return false;
                    }
                    if (solarDate.isEqual(LocalDateUtil.getNow())) {
                      return false;
                    }
                    return true;
                  })
              .collect(Collectors.toCollection(() -> new ArrayList<>(tenYearTreasuryBond.size())));
      treasuryBondRepository.saveAll(treasuryBondEntities);
      return tenYearTreasuryBond.values().stream()
          .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
          .toList();
    } finally {
      lock.unlock();
    }
  }

  private void getDataFromAPIAndSaveToDB(LocalDate startDate) {
    lock.lock();
    try {
      LocalDate finalLatestTradeDateValueInDB =
          treasuryBondRepository.findMaxSolarDate().orElseThrow();
      LocalDate finalEarliestSolarDateValueInDB =
          treasuryBondRepository.findMinSolarDate().orElseThrow();

      if (finalLatestTradeDateValueInDB.isEqual(LocalDateUtil.getNow())) {
        return;
      }
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
                    if (solarDate.isAfter(finalLatestTradeDateValueInDB)) {
                      return true;
                    }
                    return solarDate.isBefore(finalEarliestSolarDateValueInDB);
                  })
              .map(EntityConverter::convertToEntity)
              .collect(Collectors.toCollection(() -> new ArrayList<>(tenYearTreasuryBond.size())));
      treasuryBondRepository.saveAll(treasuryBondEntities);
    } finally {
      lock.unlock();
    }
  }
}
