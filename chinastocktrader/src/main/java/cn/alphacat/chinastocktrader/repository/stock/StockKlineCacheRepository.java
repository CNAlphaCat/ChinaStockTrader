package cn.alphacat.chinastocktrader.repository.stock;

import cn.alphacat.chinastocktrader.entity.stock.StockKlineCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockKlineCacheRepository extends JpaRepository<StockKlineCacheEntity, Long> {
  boolean existsByStockCode(String stockCode);
}
