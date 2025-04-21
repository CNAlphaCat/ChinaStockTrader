package cn.alphacat.chinastocktrader.service;

import cn.alphacat.chinastockdata.enums.KLineType;
import cn.alphacat.chinastockdata.market.EastMoneyMarketIndexService;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;

import cn.alphacat.chinastocktrader.model.OnePercentVolatilityFunds;
import cn.alphacat.chinastocktrader.repository.MarketIndexRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SSEIndexHistoryService {
  private final MarketService marketService;
  private final MarketIndexRepository marketIndexRepository;

  private static final String SSE_INDEX_CODE = "000001";

  public SSEIndexHistoryService(
      final MarketService marketService, final MarketIndexRepository marketIndexRepository) {
    this.marketService = marketService;
    this.marketIndexRepository = marketIndexRepository;
  }

  public List<OnePercentVolatilityFunds> getOnePercentVolatilityFunds(LocalDate startDate) {
    List<MarketIndex> shanghaiIndexHistory = getShanghaiIndexHistory(startDate);
    return shanghaiIndexHistory.stream().map(OnePercentVolatilityFunds::new).toList();
  }

  public List<MarketIndex> getShanghaiIndexHistory(LocalDate startDate) {
    Optional<LocalDate> earliestTradeDateInDB =
        marketIndexRepository.findEarliestTradeDateByIndexCode(SSE_INDEX_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      List<MarketIndex> marketIndexes = getAdataMarketIndices(startDate);
      List<MarketIndexEntity> entities =
          marketIndexes.stream().map(EntityConverter::convertToEntity).toList();
      marketIndexRepository.saveAll(entities);
      return marketIndexes;
    }
    LocalDate earliestTradeDateValueInDB = earliestTradeDateInDB.get();

    if (startDate.isAfter(earliestTradeDateValueInDB)
        || startDate.isEqual(earliestTradeDateValueInDB)) {
      List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
          marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate);
      return allByTradeDateGreaterThanOrEqualTo.stream()
          .map(EntityConverter::convertToModel)
          .toList();
    }
    List<MarketIndex> marketIndexs = getAdataMarketIndices(startDate);
    List<MarketIndexEntity> entitiesToSave =
        marketIndexs.stream()
            .filter(index -> index.getTradeDate().isBefore(earliestTradeDateValueInDB))
            .map(EntityConverter::convertToEntity)
            .toList();
    marketIndexRepository.saveAll(entitiesToSave);
    return marketIndexs;
  }

  private List<MarketIndex> getAdataMarketIndices(LocalDate startDate) {
    List<MarketIndex> marketIndexs =
        marketService.getMarketIndex(SSE_INDEX_CODE, startDate, KLineType.DAILY);
    return marketIndexs.stream()
        .filter(index -> !index.getTradeDate().isEqual(LocalDate.now()))
        .toList();
  }
}
