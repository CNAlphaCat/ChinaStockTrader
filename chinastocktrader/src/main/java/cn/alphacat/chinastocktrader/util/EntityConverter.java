package cn.alphacat.chinastocktrader.util;

import cn.alphacat.chinastockdata.model.IndexPE;
import cn.alphacat.chinastockdata.model.MarketIndex;
import cn.alphacat.chinastocktrader.entity.IndexPEEntity;
import cn.alphacat.chinastocktrader.entity.MarketIndexEntity;

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
}
