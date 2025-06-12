package cn.alphacat.chinastocktrader.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TradingSimulatorConfigurationKeyEnums {
  INITIAL_CASH("初始资金");
  private final String value;
}
