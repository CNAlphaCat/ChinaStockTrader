package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastocktrader.service.bond.ChinaStockTraderTreasuryBondService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/treasurybond")
public class TreasuryBondController {
  private final ChinaStockTraderTreasuryBondService chinaStockTraderTreasuryBondService;

  public TreasuryBondController(
      final ChinaStockTraderTreasuryBondService chinaStockTraderTreasuryBondService) {
    this.chinaStockTraderTreasuryBondService = chinaStockTraderTreasuryBondService;
  }

  @GetMapping("/getData/{startDate}")
  public List<TreasuryBond> getTenYearTreasuryBond(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    return chinaStockTraderTreasuryBondService.getSortedTreasuryBondDataList(startDate);
  }
}
