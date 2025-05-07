package cn.alphacat.chinastocktrader.service.market;

import cn.alphacat.chinastockdata.market.SZSETradeCalendarService;
import cn.alphacat.chinastockdata.model.SZSECalendar;
import cn.alphacat.chinastocktrader.entity.TradeCalendarEntity;
import cn.alphacat.chinastocktrader.repository.TradeCalendarRepository;
import cn.alphacat.chinastocktrader.util.EntityConverter;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TradeCalendarService {
  private final SZSETradeCalendarService szseTradeCalendarService;
  private final TradeCalendarRepository tradeCalendarRepository;

  private final ReentrantLock lock = new ReentrantLock();

  public TradeCalendarService(
      final SZSETradeCalendarService szseTradeCalendarService,
      final TradeCalendarRepository tradeCalendarRepository) {
    this.szseTradeCalendarService = szseTradeCalendarService;
    this.tradeCalendarRepository = tradeCalendarRepository;
  }

  public List<SZSECalendar> getSortedTradeCalendarInThirtyDays(LocalDate startDate) {
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
    return initData(startDate);
  }

  private List<SZSECalendar> initData(LocalDate startDate) {
    lock.lock();
    try {
      if (tradeCalendarRepository.count() > 0) {
        List<TradeCalendarEntity> tradeCalendarEntities =
            tradeCalendarRepository.findByTradeDateIsGreaterThanEqual(startDate);
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
            .sorted(Comparator.comparing(SZSECalendar::getTradeDate))
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
          .sorted(Comparator.comparing(SZSECalendar::getTradeDate))
          .toList();
    } finally {
      lock.unlock();
    }
  }
}
