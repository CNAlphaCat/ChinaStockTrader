package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.enums.TradingSimulatorConfigurationKeyEnums;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TradingSimulatorConfigurationDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TradingSimulatorConfigurationKeyEnums item;
  private String value;

  @ManyToOne
  @JoinColumn(name = "log_id")
  private TradingSimulatorLogEntity log;
}
