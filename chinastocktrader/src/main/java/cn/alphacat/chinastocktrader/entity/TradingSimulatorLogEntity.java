package cn.alphacat.chinastocktrader.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class TradingSimulatorLogEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String policyId;

  private BigDecimal initBalance;
  private BigDecimal currentBalance;
  private LocalDate startDate;

  @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TradingSimulatorHoldingDetailEntity> holdingDetails;

  @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TradingSimulatorConfigurationDetailEntity> configurationDetails;
}
