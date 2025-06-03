package cn.alphacat.chinastocktrader.service.simulator.policy;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.service.simulator.PolicyContext;

public interface TradingPolicy {
  TradingSimulatorLogEntity execute(PolicyContext context);
}
