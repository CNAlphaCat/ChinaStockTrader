package cn.alphacat.chinastocktrader.service.stock;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.StockExchangeMarketEnums;
import cn.alphacat.chinastockdata.model.stock.StockKline;
import cn.alphacat.chinastockdata.model.stock.StockKlineData;
import cn.alphacat.chinastockdata.stock.StockService;
import cn.alphacat.chinastocktrader.entity.stock.StockInfoEntity;
import cn.alphacat.chinastocktrader.entity.stock.StockKlineCacheEntity;
import cn.alphacat.chinastocktrader.repository.stock.StockInfoRepository;
import cn.alphacat.chinastocktrader.repository.stock.StockKlineCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockCacheService {
  private final StockService stockService;
  private final StockInfoRepository stockInfoRepository;
  private final StockKlineCacheRepository stockKlineCacheRepository;

  private static final EastMoneyQTKlineTypeEnum KLINE_TYPE_ENUM = EastMoneyQTKlineTypeEnum.DAILY;

  public StockCacheService(
      final StockService stockService,
      final StockInfoRepository stockInfoRepository,
      final StockKlineCacheRepository stockKlineCacheRepository) {
    this.stockService = stockService;
    this.stockInfoRepository = stockInfoRepository;
    this.stockKlineCacheRepository = stockKlineCacheRepository;
  }

  public void generateStockKlineCache() {
    LocalDate startDate = LocalDate.now().minusYears(1);
    List<StockInfoEntity> infoEntityList =
        stockInfoRepository.findAll().stream()
            .filter(
                stockInfo -> {
                  if (stockInfo.getExchangeMarket().equals(StockExchangeMarketEnums.BEIJING)) {
                    return false;
                  }
                  String stockCode = stockInfo.getStockCode();
                  if (stockCode.startsWith("9")) {
                    return false;
                  }
                  if (stockCode.startsWith("2")) {
                    return false;
                  }
                  return true;
                })
            .toList();
    infoEntityList.forEach(
        stockInfo -> {
          if (stockKlineCacheRepository.existsByStockCode(stockInfo.getStockCode())) {
            return;
          }
          StockKlineData stockKlineData =
              stockService.getStockKlineData(stockInfo.getStockCode(), KLINE_TYPE_ENUM, startDate);
          if (stockKlineData == null) {
            return;
          }
          List<StockKline> kLines = stockKlineData.getKLines();
          BigDecimal preKPrice = stockKlineData.getPreKPrice();

          List<StockKlineCacheEntity> kLineList = new ArrayList<>();
          for (StockKline stockKline : kLines) {
            StockKlineCacheEntity stockKlineCacheEntity =
                buildStockKlineCacheEntity(stockInfo, stockKline, preKPrice);
            kLineList.add(stockKlineCacheEntity);

            preKPrice = stockKline.getClose();
          }
          stockKlineCacheRepository.saveAll(kLineList);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    log.info("Generate Cache done");
  }

  private static StockKlineCacheEntity buildStockKlineCacheEntity(
      StockInfoEntity stockInfo, StockKline stockKline, BigDecimal preKPrice) {
    StockKlineCacheEntity stockKlineCacheEntity = new StockKlineCacheEntity();
    stockKlineCacheEntity.setStockCode(stockInfo.getStockCode());
    stockKlineCacheEntity.setDate(stockKline.getDate());
    stockKlineCacheEntity.setDateTime(stockKline.getDateTime());
    stockKlineCacheEntity.setPreKPrice(preKPrice);
    stockKlineCacheEntity.setOpen(stockKline.getOpen());
    stockKlineCacheEntity.setClose(stockKline.getClose());
    stockKlineCacheEntity.setHigh(stockKline.getHigh());
    stockKlineCacheEntity.setLow(stockKline.getLow());
    stockKlineCacheEntity.setVolume(stockKline.getVolume());
    stockKlineCacheEntity.setAmount(stockKline.getAmount());
    stockKlineCacheEntity.setChange(stockKline.getChange());
    stockKlineCacheEntity.setChangePercent(stockKline.getChangePercent());
    stockKlineCacheEntity.setTurnoverRatio(stockKline.getTurnoverRatio());
    return stockKlineCacheEntity;
  }
}
