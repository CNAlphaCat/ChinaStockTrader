package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.enums.AssetTypeEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class TradingSimulatorHoldingDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private AssetTypeEnums assetType;
  private String code;
  private String name;
  private BigDecimal buyPrice;
  private BigDecimal currentPrice;
  private BigDecimal amount;

  @ManyToOne
  @JoinColumn(name = "log_id")
  private TradingSimulatorLogEntity log;
}
