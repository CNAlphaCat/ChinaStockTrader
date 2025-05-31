package cn.alphacat.chinastocktrader.service.simulator;

import cn.alphacat.chinastocktrader.entity.TradingSimulatorConfigurationDetailEntity;
import lombok.Data;

import java.util.List;

@Data
public class PolicyContext {
  private List<TradingSimulatorConfigurationDetailEntity> configurationDetails;
}
