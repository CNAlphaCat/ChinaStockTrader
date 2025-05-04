package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TradeCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TradeCalendarRepository extends JpaRepository<TradeCalendarEntity, Long> {
  List<TradeCalendarEntity> findByTradeDateIsGreaterThanEqual(LocalDate date);
  Optional<TradeCalendarEntity> findByTradeDate(LocalDate date);
}
