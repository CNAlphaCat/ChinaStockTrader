package cn.alphacat.chinastocktrader.service.simulator.policy;

import cn.alphacat.chinastockdata.market.handler.MarketIndexConstituentHandler;
import cn.alphacat.chinastocktrader.entity.TradingSimulatorLogEntity;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorConfigurationDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorHoldingDetailRepository;
import cn.alphacat.chinastocktrader.repository.TradingSimulatorLogRepository;
import cn.alphacat.chinastocktrader.service.simulator.PolicyContext;
import lombok.Getter;
import lombok.Setter;

public class CSI300BuyLowPriceStock implements TradingPolicy {
  private final MarketIndexConstituentHandler marketIndexConstituentHandler;
  private final TradingSimulatorLogRepository tradingSimulatorLogRepository;
  private final TradingSimulatorConfigurationDetailRepository
      tradingSimulatorConfigurationDetailRepository;
  private final TradingSimulatorHoldingDetailRepository tradingSimulatorHoldingDetailRepository;

  @Getter @Setter private PolicyContext policyContext;

  public CSI300BuyLowPriceStock(
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

  @Override
  public TradingSimulatorLogEntity execute(PolicyContext context) {
    if (context.isNew()) {}

    return null;
  }
}
