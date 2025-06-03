package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.enums.AssetTypeEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class TradingSimulatorHoldingDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private AssetTypeEnums assetType;
  private String code;
  private String name;
  private BigDecimal buyPrice;
  private BigDecimal currentPrice;
  private BigDecimal amount;
  private LocalDate buyDate;
  private LocalDate sellDate;

  @ManyToOne
  @JoinColumn(name = "log_id")
  private TradingSimulatorLogEntity log;
}
