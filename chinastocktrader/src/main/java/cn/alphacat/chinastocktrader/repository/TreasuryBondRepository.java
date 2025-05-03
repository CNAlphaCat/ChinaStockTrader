package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TreasuryBondEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TreasuryBondRepository extends JpaRepository<TreasuryBondEntity, Long> {
  List<TreasuryBondEntity> findBySolarDateIsGreaterThanEqual(LocalDate solarDate);

  @Query("SELECT MIN(t.solarDate) FROM TreasuryBondEntity t")
  Optional<LocalDate> findMinSolarDate();
}
