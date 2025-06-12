package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.service.simulator.policy.TradingPolicyEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class TradingSimulatorLogEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TradingPolicyEnum policy;

  private BigDecimal initBalance;
  private BigDecimal currentBalance;
  private LocalDate startDate;

  @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TradingSimulatorHoldingDetailEntity> holdingDetails;

  @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TradingSimulatorConfigurationDetailEntity> configurationDetails;
}
