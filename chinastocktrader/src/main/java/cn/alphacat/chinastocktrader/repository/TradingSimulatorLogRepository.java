package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingSimulatorLogRepository
    extends JpaRepository<TradingSimulatorLogEntity, Long> {}
