package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.enums.AssetTypeEnums;
import cn.alphacat.chinastocktrader.enums.TradeActionEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class TradingSimulatorLogDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TradeActionEnums action;

  @Enumerated(EnumType.STRING)
  private AssetTypeEnums assetType;
  private String code;
  private String name;
  private BigDecimal buyPrice;
  private BigDecimal amount;

  @ManyToOne
  @JoinColumn(name = "executeLog_id")
  private TradingSimulatorExecuteLogEntity executeLog;
}
