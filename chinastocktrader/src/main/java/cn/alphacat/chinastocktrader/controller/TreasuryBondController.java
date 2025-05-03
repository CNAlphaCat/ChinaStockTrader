package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.bond.TreasuryBondService;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treasurybond")
public class TreasuryBondController {
  private final TreasuryBondService treasuryBondService;

  public TreasuryBondController(final TreasuryBondService treasuryBondService) {
    this.treasuryBondService = treasuryBondService;
  }

  @GetMapping("/getData/{startDate}")
  public List<TreasuryBond> getTenYearTreasuryBond(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    Map<LocalDate, TreasuryBond> tenYearTreasuryBond =
        treasuryBondService.getTreasuryBondDataMap(startDate);
    return tenYearTreasuryBond.values().stream()
        .sorted(Comparator.comparing(TreasuryBond::getSolarDate))
        .toList();
  }
}
