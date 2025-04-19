package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
  @Autowired StockService stockService;

  @RequestMapping("/test")
  public void test() {

  }
}
