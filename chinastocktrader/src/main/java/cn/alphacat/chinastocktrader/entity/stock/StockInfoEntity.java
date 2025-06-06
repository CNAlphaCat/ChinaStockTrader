package cn.alphacat.chinastocktrader.entity.stock;

import cn.alphacat.chinastockdata.enums.StockExchangeMarketEnums;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StockInfoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String stockCode;
  private String stockName;

  @Enumerated(EnumType.STRING)
  private StockExchangeMarketEnums exchangeMarket;
}
