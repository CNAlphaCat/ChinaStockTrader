package cn.alphacat.chinastocktrader.service.simulator.policy;

import cn.alphacat.chinastockdata.market.MarketIndexConstituentHandler;
import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorConfigurationDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorHoldingDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorLogRepository;

import java.util.List;
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

  public TradingPolicy generateNewPolicyById(TradingPolicyEnum tradingPolicyEnum) {
    if (tradingPolicyEnum.equals(TradingPolicyEnum.CSI_300_BUY_LOW_PRICE_STOCK)) {
      return new CSI300BuyLowPriceStock(
          this.marketIndexConstituentHandler,
          this.tradingSimulatorLogRepository,
          this.tradingSimulatorConfigurationDetailRepository,
          this.tradingSimulatorHoldingDetailRepository);
    }
    return null;
  }

  public List<TradingSimulatorLogEntity> getAllPolicies() {
    return tradingSimulatorLogRepository.findAll();
  }

  public TradingPolicy getPolicyById(Long id) {
    Optional<TradingSimulatorLogEntity> simulatorLogEntity =
        tradingSimulatorLogRepository.findById(id);
    if (simulatorLogEntity.isEmpty()) {
      return null;
    }
    TradingPolicyEnum policy = simulatorLogEntity.get().getPolicy();
    TradingPolicy newPolicyById = generateNewPolicyById(policy);
    return newPolicyById;
  }
}
