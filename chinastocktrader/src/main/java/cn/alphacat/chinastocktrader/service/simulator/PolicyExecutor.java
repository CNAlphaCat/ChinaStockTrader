package cn.alphacat.chinastocktrader.service.simulator;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorLogRepository;
import cn.alphacat.chinastocktrader.service.simulator.policy.TradingPolicy;
import org.springframework.stereotype.Service;

@Service
public class PolicyExecutor {
  private final TradingSimulatorLogRepository tradingSimulatorLogRepository;

  public PolicyExecutor(final TradingSimulatorLogRepository tradingSimulatorLogRepository) {
    this.tradingSimulatorLogRepository = tradingSimulatorLogRepository;
  }

  public TradingSimulatorLogEntity executeStrategy(
          TradingPolicy tradingPolicy, PolicyContext policyContext) {
    TradingSimulatorLogEntity log = tradingPolicy.execute(policyContext);
    tradingSimulatorLogRepository.save(log);
    return log;
  }
}
