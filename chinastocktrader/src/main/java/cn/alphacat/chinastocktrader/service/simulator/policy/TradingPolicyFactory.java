package cn.alphacat.chinastocktrader.service.simulator.policy;

import cn.alphacat.chinastockdata.market.MarketIndexConstituentHandler;
import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorConfigurationDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorHoldingDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorLogRepository;
import cn.alphacat.chinastocktrader.service.simulator.PolicyContext;

import java.util.Optional;

public class TradingPolicyFactory {
  private final MarketIndexConstituentHandler marketIndexConstituentHandler;
  private final TradingSimulatorLogRepository tradingSimulatorLogRepository;
  private final TradingSimulatorConfigurationDetailRepository
      tradingSimulatorConfigurationDetailRepository;
  private final TradingSimulatorHoldingDetailRepository tradingSimulatorHoldingDetailRepository;

  public TradingPolicyFactory(
      final MarketIndexConstituentHandler marketIndexConstituentHandler,
      final TradingSimulatorLogRepository tradingSimulatorLogRepository,
      final TradingSimulatorConfigurationDetailRepository
          tradingSimulatorConfigurationDetailRepository,
      final TradingSimulatorHoldingDetailRepository tradingSimulatorHoldingDetailRepository) {
    this.marketIndexConstituentHandler = marketIndexConstituentHandler;
    this.tradingSimulatorLogRepository = tradingSimulatorLogRepository;
    this.tradingSimulatorConfigurationDetailRepository =
        tradingSimulatorConfigurationDetailRepository;
    this.tradingSimulatorHoldingDetailRepository = tradingSimulatorHoldingDetailRepository;
  }

  public TradingPolicy generateNewPolicyById(Class<? extends TradingPolicy> policyClass) {
    if (policyClass == CSI300BuyLowPriceStock.class) {
      return new CSI300BuyLowPriceStock(
          this.marketIndexConstituentHandler,
          this.tradingSimulatorLogRepository,
          this.tradingSimulatorConfigurationDetailRepository,
          this.tradingSimulatorHoldingDetailRepository,
          new PolicyContext());
    }
    return null;
  }

  public TradingPolicy getPolicyById(Long id) {
    Optional<TradingSimulatorLogEntity> simulatorLogEntity =
        tradingSimulatorLogRepository.findById(id);
    if (simulatorLogEntity.isEmpty()) {
      return null;
    }
    String policyId = simulatorLogEntity.get().getPolicyId();
    return new CSI300BuyLowPriceStock(
        this.marketIndexConstituentHandler,
        this.tradingSimulatorLogRepository,
        this.tradingSimulatorConfigurationDetailRepository,
        this.tradingSimulatorHoldingDetailRepository,
        new PolicyContext());
  }
}
