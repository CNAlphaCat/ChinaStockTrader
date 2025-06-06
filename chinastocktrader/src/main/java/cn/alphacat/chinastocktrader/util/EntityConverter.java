package cn.alphacat.chinastocktrader.util;

import cn.alphacat.chinastockdata.model.SZSECalendar;
import cn.alphacat.chinastockdata.model.marketindex.IndexPE;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastockdata.model.stock.StockInfo;
import cn.alphacat.chinastocktrader.entity.*;

import cn.alphacat.chinastocktrader.entity.stock.StockInfoEntity;
import cn.alphacat.chinastockdata.enums.StockExchangeMarketEnums;
import cn.alphacat.chinastocktrader.view.StockLimitView;
import org.springframework.stereotype.Component;

@Component
public class EntityConverter {
  public static MarketIndexEntity convertToEntity(MarketIndex adataMarketIndex) {
    MarketIndexEntity entity = new MarketIndexEntity();
    entity.setIndexCode(adataMarketIndex.getIndexCode());
    entity.setTradeDate(adataMarketIndex.getTradeDate());
    entity.setTradeTime(adataMarketIndex.getTradeTime());
    entity.setOpen(adataMarketIndex.getOpen());
    entity.setHigh(adataMarketIndex.getHigh());
    entity.setLow(adataMarketIndex.getLow());
    entity.setClose(adataMarketIndex.getClose());
    entity.setVolume(adataMarketIndex.getVolume());
    entity.setAmount(adataMarketIndex.getAmount());
    entity.setChange(adataMarketIndex.getChange());
    entity.setChangePct(adataMarketIndex.getChangePct());
    return entity;
  }

  public static MarketIndex convertToModel(MarketIndexEntity entity) {
    MarketIndex model = new MarketIndex();
    model.setIndexCode(entity.getIndexCode());
    model.setTradeDate(entity.getTradeDate());
    model.setTradeTime(entity.getTradeTime());
    model.setOpen(entity.getOpen());
    model.setHigh(entity.getHigh());
    model.setLow(entity.getLow());
    model.setClose(entity.getClose());
    model.setVolume(entity.getVolume());
    model.setAmount(entity.getAmount());
    model.setChange(entity.getChange());
    model.setChangePct(entity.getChangePct());
    return model;
  }

  public static IndexPE convertToModel(IndexPEEntity entity) {
    return IndexPE.builder()
        .date(entity.getDate())
        .indexCode(entity.getIndexCode())
        .close(entity.getClose())
        .lyrPe(entity.getLyrPe())
        .addLyrPe(entity.getAddLyrPe())
        .middleLyrPe(entity.getMiddleLyrPe())
        .ttmPe(entity.getTtmPe())
        .addTtmPe(entity.getAddTtmPe())
        .middleTtmPe(entity.getMiddleTtmPe())
        .build();
  }

  public static IndexPEEntity convertToEntity(IndexPE model) {
    IndexPEEntity entity = new IndexPEEntity();
    entity.setDate(model.getDate());
    entity.setIndexCode(model.getIndexCode());
    entity.setClose(model.getClose());
    entity.setLyrPe(model.getLyrPe());
    entity.setAddLyrPe(model.getAddLyrPe());
    entity.setMiddleLyrPe(model.getMiddleLyrPe());
    entity.setTtmPe(model.getTtmPe());
    entity.setAddTtmPe(model.getAddTtmPe());
    entity.setMiddleTtmPe(model.getMiddleTtmPe());
    return entity;
  }

  public static TreasuryBondEntity convertToEntity(TreasuryBond model) {
    TreasuryBondEntity entity = new TreasuryBondEntity();
    entity.setSolarDate(model.getSolarDate());
    entity.setFiveYearTreasuryBondYield(model.getFiveYearTreasuryBondYield());
    entity.setTenYearTreasuryBondYield(model.getTenYearTreasuryBondYield());
    entity.setThirtyYearTreasuryBondYield(model.getThirtyYearTreasuryBondYield());
    entity.setTwoYearTreasuryBondYield(model.getTwoYearTreasuryBondYield());
    entity.setTwoYearMinusTenYearTreasuryBondYield(model.getTwoYearMinusTenYearTreasuryBondYield());
    entity.setTwoYearUSTreasuryBondYield(model.getTwoYearUSTreasuryBondYield());
    entity.setFiveYearUSTreasuryBondYield(model.getFiveYearUSTreasuryBondYield());
    entity.setTenYearUSTreasuryBondYield(model.getTenYearUSTreasuryBondYield());
    entity.setThirtyYearUSTreasuryBondYield(model.getThirtyYearUSTreasuryBondYield());
    entity.setTwoYearMinusTenYearUSTreasuryBondYield(
        model.getTwoYearMinusTenYearUSTreasuryBondYield());
    entity.setChinaGDPGrowthRate(model.getChinaGDPGrowthRate());
    entity.setUsGDPGrowthRate(model.getUsGDPGrowthRate());
    return entity;
  }

  public static TreasuryBond convertToModel(TreasuryBondEntity entity) {
    return TreasuryBond.builder()
        .solarDate(entity.getSolarDate())
        .fiveYearTreasuryBondYield(entity.getFiveYearTreasuryBondYield())
        .tenYearTreasuryBondYield(entity.getTenYearTreasuryBondYield())
        .thirtyYearTreasuryBondYield(entity.getThirtyYearTreasuryBondYield())
        .twoYearTreasuryBondYield(entity.getTwoYearTreasuryBondYield())
        .twoYearMinusTenYearTreasuryBondYield(entity.getTwoYearMinusTenYearTreasuryBondYield())
        .twoYearUSTreasuryBondYield(entity.getTwoYearUSTreasuryBondYield())
        .fiveYearUSTreasuryBondYield(entity.getFiveYearUSTreasuryBondYield())
        .tenYearUSTreasuryBondYield(entity.getTenYearUSTreasuryBondYield())
        .thirtyYearUSTreasuryBondYield(entity.getThirtyYearUSTreasuryBondYield())
        .twoYearMinusTenYearUSTreasuryBondYield(entity.getTwoYearMinusTenYearUSTreasuryBondYield())
        .chinaGDPGrowthRate(entity.getChinaGDPGrowthRate())
        .usGDPGrowthRate(entity.getUsGDPGrowthRate())
        .build();
  }

  public static TradeCalendarEntity convertToEntity(SZSECalendar tradeCalendar) {
    TradeCalendarEntity entity = new TradeCalendarEntity();
    entity.setTradeDate(tradeCalendar.getTradeDate());
    entity.setTradeStatus(tradeCalendar.getTradeStatus());
    return entity;
  }

  public static SZSECalendar convertToModel(TradeCalendarEntity entity) {
    SZSECalendar tradeCalendar = new SZSECalendar();
    tradeCalendar.setTradeDate(entity.getTradeDate());
    tradeCalendar.setTradeStatus(entity.getTradeStatus());
    return tradeCalendar;
  }

  public static StockLimitEntity convertToEntity(StockLimitView model) {
    StockLimitEntity entity = new StockLimitEntity();
    entity.setTradeDate(model.getTradeDate());
    entity.setLimitUpCount(model.getLimitUpCount());
    entity.setLimitDownCount(model.getLimitDownCount());
    return entity;
  }

  public static StockLimitView convertToModel(StockLimitEntity entity) {
    StockLimitView model = new StockLimitView();
    model.setTradeDate(entity.getTradeDate());
    model.setLimitUpCount(entity.getLimitUpCount());
    model.setLimitDownCount(entity.getLimitDownCount());
    return model;
  }

  public static StockInfoEntity convertToEntity(StockInfo stockInfo) {
    StockInfoEntity entity = new StockInfoEntity();
    entity.setStockCode(stockInfo.getStockCode());
    entity.setStockName(stockInfo.getStockName());
    entity.setExchangeMarket(stockInfo.getExchangeMarket());
    return entity;
  }

  public static StockInfo convertToModel(StockInfoEntity entity) {
    StockInfo model = new StockInfo();
    model.setStockCode(entity.getStockCode());
    model.setStockName(entity.getStockName());
    model.setExchangeMarket(entity.getExchangeMarket());
    return model;
  }
}
