package cn.alphacat.chinastocktrader.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class MarketIndexEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String indexCode;
  private LocalDate tradeDate;
  private LocalDateTime tradeTime;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private BigDecimal volume;
  private BigDecimal amount;

  @Column(name = "indexPointChange")
  private BigDecimal change;

  private BigDecimal changePct;
}
