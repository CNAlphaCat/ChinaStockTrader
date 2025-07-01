package cn.alphacat.chinastocktrader.entity.future;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class FutureHistoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal volume;
  private BigDecimal amount;
  private BigDecimal holdingVolume;
  private BigDecimal holdingVolumeChange;
  private BigDecimal close;
  private BigDecimal settlementToday;
  private BigDecimal settlementYesterday;
  private BigDecimal change1;
  private BigDecimal change2;
  private BigDecimal delta;
  private LocalDate date;
}
