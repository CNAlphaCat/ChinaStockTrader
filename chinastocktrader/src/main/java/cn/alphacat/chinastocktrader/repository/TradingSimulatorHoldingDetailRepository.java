package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorHoldingDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingSimulatorHoldingDetailRepository
    extends JpaRepository<TradingSimulatorHoldingDetailEntity, Long> {}
