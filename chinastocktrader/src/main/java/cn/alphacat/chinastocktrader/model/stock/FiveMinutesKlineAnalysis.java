package cn.alphacat.chinastocktrader.model.stock;

import cn.alphacat.chinastockdata.model.stock.StockKline;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

@Data
@Builder
public class FiveMinutesKlineAnalysis {
  private String stockCode;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer increaseFiveMinuteKlineCount;
  private Integer decreaseFiveMinuteKlineCount;
  private Integer increaseThirtyMinuteKlineCount;
  private Integer decreaseThirtyMinuteKlineCount;
  private PriorityQueue<StockKline> topVolumeKlines;
  private BigDecimal expectedRisingAmountInOneMinute;
  private String displayRisingAmountInOneMinute;
}
