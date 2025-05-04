package cn.alphacat.chinastocktrader.service.market;

import cn.alphacat.chinastockdata.enums.TradeStatusEnum;
import cn.alphacat.chinastockdata.market.SZSETradeCalendarService;
import cn.alphacat.chinastockdata.model.SZSECalendar;
import cn.alphacat.chinastocktrader.entity.TradeCalendarEntity;
import cn.alphacat.chinastocktrader.repository.TradeCalendarRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TradeCalendarService {
  private final SZSETradeCalendarService szseTradeCalendarService;
  private final TradeCalendarRepository tradeCalendarRepository;

  public TradeCalendarService(
      final SZSETradeCalendarService szseTradeCalendarService,
      final TradeCalendarRepository tradeCalendarRepository) {
    this.szseTradeCalendarService = szseTradeCalendarService;
    this.tradeCalendarRepository = tradeCalendarRepository;
  }

  public List<SZSECalendar> getTradeCalendarInThirtyDays(LocalDate startDate) {
    List<TradeCalendarEntity> tradeCalendarEntities =
        tradeCalendarRepository.findByTradeDateIsGreaterThanEqual(startDate);

    if (!tradeCalendarEntities.isEmpty()) {
      return tradeCalendarEntities.stream()
          .map(EntityConverter::convertToModel)
          .filter(
              calendar -> {
                LocalDate tradeDate = calendar.getTradeDate();
                if (tradeDate.isBefore(startDate)) {
                  return false;
                }
                if (tradeDate.isAfter(LocalDateUtil.getNow())) {
                  return false;
                }
                return true;
              })
          .toList();
    }
    int year = LocalDateUtil.getNow().getYear();
    List<SZSECalendar> tradeCalendar = szseTradeCalendarService.getTradeCalendar(year);

    List<TradeCalendarEntity> entitiesSaveToDB =
        tradeCalendar.stream().map(EntityConverter::convertToEntity).toList();
    tradeCalendarRepository.saveAll(entitiesSaveToDB);
    return tradeCalendar.stream()
        .filter(
            calendar -> {
              LocalDate tradeDate = calendar.getTradeDate();
              if (tradeDate.isBefore(startDate)) {
                return false;
              }
              if (tradeDate.isAfter(LocalDateUtil.getNow())) {
                return false;
              }
              return true;
            })
        .toList();
  }

  public SZSECalendar getTradeCalendar(LocalDate date) {
    Optional<TradeCalendarEntity> byTradeDate = tradeCalendarRepository.findByTradeDate(date);
    if (byTradeDate.isPresent()) {
      TradeCalendarEntity tradeCalendarEntity = byTradeDate.get();
      return EntityConverter.convertToModel(tradeCalendarEntity);
    }
    int year = date.getYear();
    List<SZSECalendar> tradeCalendar = szseTradeCalendarService.getTradeCalendar(year);

    List<TradeCalendarEntity> tradeCalendarEntities = new ArrayList<>();
    for (SZSECalendar calendar : tradeCalendar) {
      TradeCalendarEntity tradeCalendarEntity = EntityConverter.convertToEntity(calendar);
      tradeCalendarEntities.add(tradeCalendarEntity);
    }
    tradeCalendarRepository.saveAll(tradeCalendarEntities);

    return tradeCalendar.stream()
        .filter(calendar -> calendar.getTradeDate().equals(date))
        .findFirst()
        .orElse(null);
  }
}
