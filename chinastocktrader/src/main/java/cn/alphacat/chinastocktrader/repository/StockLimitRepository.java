package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.StockLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface StockLimitRepository extends JpaRepository<StockLimitEntity, Long> {
  @Query("SELECT MAX(m.tradeDate) FROM StockLimitEntity m")
  Optional<LocalDate> findMaxTradeDate();
}
