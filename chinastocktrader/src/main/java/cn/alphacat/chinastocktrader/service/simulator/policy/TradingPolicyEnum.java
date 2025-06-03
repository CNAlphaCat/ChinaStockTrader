package cn.alphacat.chinastocktrader.service.simulator.policy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TradingPolicyEnum {
  CSI_300_BUY_LOW_PRICE_STOCK("沪深300低价股票交换策略");
  private final String name;
}
