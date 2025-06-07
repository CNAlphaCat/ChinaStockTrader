package cn.alphacat.chinastocktrader.entity.stock;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class StockKlineCacheEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String stockCode;

  private LocalDate date;
  private LocalDateTime dateTime;

  private BigDecimal preKPrice;
  private BigDecimal open;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal volume;
  private BigDecimal amount;

  @Column(name = "stockPriceChange")
  private BigDecimal change;
  private BigDecimal changePercent;
  private BigDecimal turnoverRatio;
}
