package cn.alphacat.chinastocktrader.repository.stock;

import cn.alphacat.chinastocktrader.entity.stock.StockInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockInfoRepository extends JpaRepository<StockInfoEntity, Long> {
  boolean existsByStockCode(String stockCode);
}
