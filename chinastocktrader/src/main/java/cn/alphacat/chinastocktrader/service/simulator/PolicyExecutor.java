package cn.alphacat.chinastocktrader.service.simulator;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogDetailEntity;
import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PolicyExecutor {
  private final TradingSimulatorLogRepository tradingSimulatorLogRepository;

  public PolicyExecutor(final TradingSimulatorLogRepository tradingSimulatorLogRepository) {
    this.tradingSimulatorLogRepository = tradingSimulatorLogRepository;
  }

  public TradingSimulatorLogEntity executeStrategy(String strategyId, PolicyContext policyContext) {
    TradingPolicy tradingPolicy = TradingPolicyFactory.getPolicyById(strategyId);
    TradingSimulatorLogEntity log = tradingPolicy.execute(policyContext);
    tradingSimulatorLogRepository.save(log);
    return log;
  }
}
