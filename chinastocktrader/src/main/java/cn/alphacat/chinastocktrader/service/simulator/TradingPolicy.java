package cn.alphacat.chinastocktrader.service.simulator;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;

public interface TradingPolicy {
    String getId();
    String getName();
    TradingSimulatorLogEntity execute(PolicyContext context);
}
