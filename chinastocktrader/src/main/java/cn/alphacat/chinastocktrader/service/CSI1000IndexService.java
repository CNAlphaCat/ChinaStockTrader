package cn.alphacat.chinastocktrader.service;

import cn.alphacat.chinastockdata.enums.KLineTypeEnum;
import cn.alphacat.chinastockdata.market.MarketService;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;
import cn.alphacat.chinastocktrader.repository.MarketIndexRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CSI1000IndexService {
  private final MarketService marketService;
  private final MarketIndexRepository marketIndexRepository;

  private static final String CSI1000_CODE = "000852";

  public CSI1000IndexService(
      final MarketService marketService, final MarketIndexRepository marketIndexRepository) {
    this.marketService = marketService;
    this.marketIndexRepository = marketIndexRepository;
  }

  public List<MarketIndex> getCSI1000IndexDaily(LocalDate startDate) {
    Optional<LocalDate> earliestTradeDateInDB =
        marketIndexRepository.findEarliestTradeDateByIndexCode(CSI1000_CODE);
    if (earliestTradeDateInDB.isEmpty()) {
      List<MarketIndex> marketIndexes = getCSI1000IndexDailyFromAPI(startDate);
      List<MarketIndexEntity> entities =
          marketIndexes.stream()
              .filter(MarketIndex::checkValid)
              .map(EntityConverter::convertToEntity)
              .toList();
      marketIndexRepository.saveAll(entities);
      return marketIndexes;
    }
    LocalDate earliestTradeDateValueInDB = earliestTradeDateInDB.get();

    if (startDate.isAfter(earliestTradeDateValueInDB)
        || startDate.isEqual(earliestTradeDateValueInDB)) {
      List<MarketIndexEntity> allByTradeDateGreaterThanOrEqualTo =
          marketIndexRepository.findAllByTradeDateGreaterThanOrEqualTo(startDate, CSI1000_CODE);
      return allByTradeDateGreaterThanOrEqualTo.stream()
          .map(EntityConverter::convertToModel)
          .toList();
    }
    List<MarketIndex> marketIndexs = getCSI1000IndexDailyFromAPI(startDate);
    List<MarketIndexEntity> entitiesToSave =
        marketIndexs.stream()
            .filter(
                index ->
                    index.getTradeDate().isBefore(earliestTradeDateValueInDB) && index.checkValid())
            .map(EntityConverter::convertToEntity)
            .toList();
    marketIndexRepository.saveAll(entitiesToSave);
    return marketIndexs;
  }

  private List<MarketIndex> getCSI1000IndexDailyFromAPI(LocalDate startDate) {
    List<MarketIndex> marketIndex =
        marketService.getMarketIndex(CSI1000_CODE, startDate, KLineTypeEnum.DAILY);
    if (TimeUtil.isAfterStockCloseTime()) {
      return marketIndex;
    }
    return marketIndex.stream()
        .filter(index -> !index.getTradeDate().isEqual(LocalDate.now()))
        .toList();
  }
}
