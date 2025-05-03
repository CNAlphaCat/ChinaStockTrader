package cn.alphacat.chinastocktrader.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class TreasuryBondEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate solarDate;
  private BigDecimal fiveYearTreasuryBondYield;
  private BigDecimal tenYearTreasuryBondYield;
  private BigDecimal thirtyYearTreasuryBondYield;
  private BigDecimal twoYearTreasuryBondYield;

  private BigDecimal twoYearMinusTenYearTreasuryBondYield;

  private BigDecimal twoYearUSTreasuryBondYield;
  private BigDecimal fiveYearUSTreasuryBondYield;
  private BigDecimal tenYearUSTreasuryBondYield;
  private BigDecimal thirtyYearUSTreasuryBondYield;

  private BigDecimal twoYearMinusTenYearUSTreasuryBondYield;

  private BigDecimal chinaGDPGrowthRate;
  private BigDecimal usGDPGrowthRate;
}
