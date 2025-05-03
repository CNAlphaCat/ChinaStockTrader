package cn.alphacat.chinastocktrader.util;

import cn.alphacat.chinastockdata.model.IndexPE;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastockdata.model.bond.TreasuryBond;
import cn.alphacat.chinastocktrader.entity.IndexPEEntity;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;

import cn.alphacat.chinastocktrader.entity.TreasuryBondEntity;
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
    return TreasuryBondEntity.builder()
        .solarDate(model.getSolarDate())
        .fiveYearTreasuryBondYield(model.getFiveYearTreasuryBondYield())
        .tenYearTreasuryBondYield(model.getTenYearTreasuryBondYield())
        .thirtyYearTreasuryBondYield(model.getThirtyYearTreasuryBondYield())
        .twoYearTreasuryBondYield(model.getTwoYearTreasuryBondYield())
        .twoYearMinusTenYearTreasuryBondYield(model.getTwoYearMinusTenYearTreasuryBondYield())
        .twoYearUSTreasuryBondYield(model.getTwoYearUSTreasuryBondYield())
        .fiveYearUSTreasuryBondYield(model.getFiveYearUSTreasuryBondYield())
        .tenYearUSTreasuryBondYield(model.getTenYearUSTreasuryBondYield())
        .thirtyYearUSTreasuryBondYield(model.getThirtyYearUSTreasuryBondYield())
        .twoYearMinusTenYearUSTreasuryBondYield(model.getTwoYearMinusTenYearUSTreasuryBondYield())
        .chinaGDPGrowthRate(model.getChinaGDPGrowthRate())
        .usGDPGrowthRate(model.getUsGDPGrowthRate())
        .build();
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
}
