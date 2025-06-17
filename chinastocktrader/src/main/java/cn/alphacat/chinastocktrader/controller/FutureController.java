package cn.alphacat.chinastocktrader.controller;

import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import cn.alphacat.chinastocktrader.service.future.ChinaStockTraderFutureService;
import cn.alphacat.chinastocktrader.service.future.IFFutureService;
import cn.alphacat.chinastocktrader.service.future.IMFutureService;
import cn.alphacat.chinastocktrader.view.future.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/api/future")
public class FutureController {
  private final ChinaStockTraderFutureService chinaStockTraderFutureService;
  private final IFFutureService ifFutureService;
  private final IMFutureService imFutureService;

  public FutureController(
      final ChinaStockTraderFutureService chinaStockTraderFutureService,
      final IFFutureService ifFutureService,
      final IMFutureService imFutureService) {
    this.chinaStockTraderFutureService = chinaStockTraderFutureService;
    this.ifFutureService = ifFutureService;
    this.imFutureService = imFutureService;
  }

  @PostMapping("/getIFFutureHistory")
  public List<FutureHistory> getIFFutureHistory(@RequestBody FutureHistoryRequestView view) {
    return chinaStockTraderFutureService.getIFFutureHistory(
        view.getBeginDate(), view.getEndDate(), view.getKlt(), view.getFqt());
  }

  @PostMapping("/getDiffBetweenIFAndIndex")
  public List<DiffBetweenIFAndIndexView> getDiffBetweenIFAndIndex(
      @RequestBody DiffBetweenIFAndIndexRequestView view) {
    return ifFutureService.getDiffBetweenIFAndIndex(view.getStartYear(), view.getStartMonth());
  }

  @GetMapping("/download/DiffBetweenIMAndIndex")
  public ResponseEntity<ByteArrayResource> downloadDiffBetweenIMAndIndex(
      @RequestParam int year, @RequestParam Month month) throws Exception {
    byte[] excelBytes = imFutureService.exportDiffToExcel(year, month);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "IM_History_From_" + year + "_" + month + ".xlsx");

    return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(excelBytes));
  }

  @PostMapping("/getDiffBetweenIMAndIndex")
  public List<DiffBetweenIMAndIndexView> getDiffBetweenIMAndIndex(
      @RequestBody DiffBetweenIMAndIndexRequestView view) {
    return imFutureService.getDiffBetweenIMAndIndex(view.getStartYear(), view.getStartMonth());
  }

  @RequestMapping("/getNoSmoothIFFutureHistory")
  public List<FutureHistory> getNoSmoothIFFutureHistory(@RequestBody NoSmoothIFFutureView view) {
    return ifFutureService.getNoSmoothIFFutureHistory(view.getStartYear(), view.getStartMonth());
  }

  /*
   * 获取所有期货品种信息
   */
  @RequestMapping("/futuresBaseInfo")
  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return chinaStockTraderFutureService.getFuturesBaseInfo();
  }

  /*
   * 获取IM和IF的比值
   */
  @RequestMapping("/currentIMDivideIFPrice")
  public BigDecimal getCurrentIMDivideIFPrice() {
    return chinaStockTraderFutureService.getCurrentIMDivideIFPrice();
  }
}
