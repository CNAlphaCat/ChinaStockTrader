package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastockdata.enums.TradeStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class TradeCalendarEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private TradeStatusEnum tradeStatus;
  private LocalDate tradeDate;
}
