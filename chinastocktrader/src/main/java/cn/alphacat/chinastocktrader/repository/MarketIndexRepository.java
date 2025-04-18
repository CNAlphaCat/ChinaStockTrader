package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarketIndexRepository extends JpaRepository<MarketIndexEntity, Long> {
  @Query("SELECT MIN(a.tradeDate) FROM MarketIndexEntity a WHERE a.indexCode = :indexCode")
  Optional<LocalDate> findEarliestTradeDateByIndexCode(@Param("indexCode") String indexCode);

  @Query("SELECT a FROM MarketIndexEntity a WHERE a.tradeDate >= :tradeDate")
  List<MarketIndexEntity> findAllByTradeDateGreaterThanOrEqualTo(
      @Param("tradeDate") LocalDate tradeDate);
}
