package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorConfigurationDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingSimulatorConfigurationDetailRepository
    extends JpaRepository<TradingSimulatorConfigurationDetailEntity, Long> {}
