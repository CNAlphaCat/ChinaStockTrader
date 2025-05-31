package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingSimulatorLogDetailRepository
    extends JpaRepository<TradingSimulatorLogDetailEntity, Long> {}
