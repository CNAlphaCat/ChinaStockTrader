package cn.alphacat.chinastocktrader.service.simulator;

import java.util.HashMap;
import java.util.Map;

public class TradingPolicyFactory {
  private static final Map<String, TradingPolicy> POLICY_MAP = new HashMap<>();

  public static TradingPolicy getPolicyById(String id) {
    return POLICY_MAP.get(id);
  }
}
