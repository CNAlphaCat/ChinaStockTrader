package cn.alphacat.chinastocktrader.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

  @RequestMapping("/test")
  public void test() throws Exception {

  }
}
