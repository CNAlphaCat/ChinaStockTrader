package cn.alphacat.chinastocktrader.service.simulator;

import cn.alphacat.chinastocktrader.enums.TradingSimulatorConfigurationKeyEnums;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class PolicyContext {
  private Long logId;
  private BigDecimal initBalance;
  private Map<TradingSimulatorConfigurationKeyEnums, String> configuration;

  public boolean isNew() {
    return this.logId == null;
  }
}
