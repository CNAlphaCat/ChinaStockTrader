package cn.alphacat.chinastocktrader.model;

import cn.alphacat.chinastockdata.model.IndexPE;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@Builder
public class EquityPremiumIndex {
  private LocalDate date;
  private BigDecimal equityPremiumIndex;
  private BigDecimal percentile;

  public static EquityPremiumIndex build(IndexPE indexPE, TreasuryBond treasuryBond) {
    if (indexPE == null || treasuryBond == null) {
      return null;
    }
    LocalDate indexPEDate = indexPE.getDate();
    if (indexPEDate == null) {
      return null;
    }

    if (!indexPEDate.equals(treasuryBond.getSolarDate())) {
      return null;
    }

    BigDecimal tenYearTreasuryBondYield = treasuryBond.getTenYearTreasuryBondYield();
    if (tenYearTreasuryBondYield == null) {
      return null;
    }

    BigDecimal addTtmPe = indexPE.getAddTtmPe();
    if (addTtmPe == null) {
      return null;
    }

    BigDecimal inverseAddTtmPe = BigDecimal.ONE.divide(addTtmPe, 10, RoundingMode.HALF_UP);
    BigDecimal equityPremiumIndex =
        inverseAddTtmPe
            .multiply(new BigDecimal(100))
            .subtract(tenYearTreasuryBondYield)
            .setScale(4, RoundingMode.HALF_UP);
    return EquityPremiumIndex.builder()
        .date(indexPEDate)
        .equityPremiumIndex(equityPremiumIndex)
        .build();
  }
}
