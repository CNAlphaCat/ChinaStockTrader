package cn.alphacat.chinastocktrader.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class IndexPEEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String indexCode;
  private LocalDate date;
  // 指数
  private BigDecimal close;
  // 等权静态市盈率
  private BigDecimal lyrPe;
  // 静态市盈率
  private BigDecimal addLyrPe;
  // 静态市盈率中位数
  private BigDecimal middleLyrPe;
  // 等权滚动市盈率
  private BigDecimal ttmPe;
  // 滚动市盈率
  private BigDecimal addTtmPe;
  // 滚动市盈率中位数
  private BigDecimal middleTtmPe;
}
