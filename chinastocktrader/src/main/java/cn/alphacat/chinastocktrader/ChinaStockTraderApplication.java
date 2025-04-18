package cn.alphacat.chinastocktrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.alphacat.chinastockdata", "cn.alphacat.chinastocktrader"})
public class ChinaStockTraderApplication {
  public static void main(String[] args) {
    SpringApplication.run(ChinaStockTraderApplication.class, args);
  }
}
