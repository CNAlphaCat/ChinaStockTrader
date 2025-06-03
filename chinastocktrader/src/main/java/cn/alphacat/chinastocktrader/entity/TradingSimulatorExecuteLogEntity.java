package cn.alphacat.chinastocktrader.entity;

import cn.alphacat.chinastocktrader.enums.ExecutionStatusEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class TradingSimulatorExecuteLogEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ExecutionStatusEnums status;

  private String message;
  private LocalDateTime executeDatetime;

  @OneToMany(mappedBy = "executeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TradingSimulatorLogDetailEntity> logDetails;
}
