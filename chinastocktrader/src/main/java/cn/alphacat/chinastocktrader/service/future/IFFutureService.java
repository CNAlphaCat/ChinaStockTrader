package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.service.marketindex.CSI300IndexService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.future.DiffBetweenIFAndIndexView;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IFFutureService {
  private final FutureService featureService;
  private final CSI300IndexService csi300IndexService;

  private static final String IF_MAIN_FUTURE_CODE = "8.040130";

  public IFFutureService(
      final FutureService featureService, final CSI300IndexService csi300IndexService) {
    this.featureService = featureService;
    this.csi300IndexService = csi300IndexService;
  }

  public List<FutureMarketOverview> getFuturesBaseInfo() {
    return featureService.getFuturesBaseInfo();
  }

  public List<FutureHistory> getFutureHistory(
      LocalDate beginDate,
      LocalDate endDate,
      EastMoneyQTKlineTypeEnum klt,
      EastMoneyQTKlineWeightingEnum fqt) {
    return featureService.getFutureHistory(IF_MAIN_FUTURE_CODE, beginDate, endDate, klt, fqt);
  }

  public List<FutureHistory> getTodayFutureHistory() {
    return featureService.getFutureHistory(
        IF_MAIN_FUTURE_CODE,
        LocalDateUtil.getNow(),
        LocalDateUtil.getNow(),
        EastMoneyQTKlineTypeEnum.ONE_MINUTE,
        EastMoneyQTKlineWeightingEnum.NON_WEIGHTING);
  }

  public List<DiffBetweenIFAndIndexView> getDiffBetweenIFAndIndex(int startYear, Month startMonth) {
    List<FutureHistory> noSmoothIMFutureHistory = getNoSmoothIFFutureHistory(startYear, startMonth);
    Map<LocalDate, FutureHistory> noSmoothIMFutureHistoryMap =
        noSmoothIMFutureHistory.stream()
            .collect(Collectors.toMap(FutureHistory::getDate, futureHistory -> futureHistory));
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    List<MarketIndex> csi300IndexDaily = csi300IndexService.getCSI300IndexDaily(startDate);
    Map<LocalDate, MarketIndex> csi300IndexDailyMap =
        csi300IndexDaily.stream()
            .collect(Collectors.toMap(MarketIndex::getTradeDate, marketIndex -> marketIndex));
    List<DiffBetweenIFAndIndexView> result = new ArrayList<>();
    for (LocalDate date : csi300IndexDailyMap.keySet()) {
      FutureHistory futureHistory = noSmoothIMFutureHistoryMap.get(date);
      if (futureHistory == null) {
        continue;
      }
      MarketIndex csi1000Index = csi300IndexDailyMap.get(date);
      if (csi1000Index == null) {
        continue;
      }
      DiffBetweenIFAndIndexView diffBetweenIFAndIndexView =
          getDiffBetweenIFAndIndexView(date, futureHistory, csi1000Index);

      result.add(diffBetweenIFAndIndexView);
    }
    return result.stream()
        .sorted(Comparator.comparing(DiffBetweenIFAndIndexView::getDate))
        .toList();
  }

  private static DiffBetweenIFAndIndexView getDiffBetweenIFAndIndexView(
      LocalDate date, FutureHistory futureHistory, MarketIndex csi1000Index) {
    DiffBetweenIFAndIndexView diffBetweenIFAndIndexView = new DiffBetweenIFAndIndexView();

    BigDecimal ifClose = futureHistory.getClose();
    BigDecimal csi1000IndexClose = csi1000Index.getClose();

    diffBetweenIFAndIndexView.setDate(date);
    diffBetweenIFAndIndexView.setDiff(ifClose.subtract(csi1000IndexClose));
    diffBetweenIFAndIndexView.setCsi300ClosePrice(csi1000IndexClose);
    diffBetweenIFAndIndexView.setIfCode(futureHistory.getCode());
    diffBetweenIFAndIndexView.setIfOpenPrice(futureHistory.getOpen());
    diffBetweenIFAndIndexView.setIfClosePrice(ifClose);
    diffBetweenIFAndIndexView.setIfHighPrice(futureHistory.getHigh());
    diffBetweenIFAndIndexView.setIfLowPrice(futureHistory.getLow());
    diffBetweenIFAndIndexView.setIfVolume(futureHistory.getVolume());
    diffBetweenIFAndIndexView.setIfAmount(futureHistory.getAmount());
    diffBetweenIFAndIndexView.setDelta(futureHistory.getDelta());

    return diffBetweenIFAndIndexView;
  }

  public byte[] exportDiffToExcel(int startYear, Month startMonth) throws IOException {
    List<DiffBetweenIFAndIndexView> diffBetweenIFAndIndexViews =
        getDiffBetweenIFAndIndex(startYear, startMonth);

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("IF与指数点差");
      Row headerRow = sheet.createRow(0);
      String[] headers = {
        "日期",
        "IF品种",
        "IF与指数收盘价价差",
        "IF开盘价",
        "IF收盘价",
        "IF最高价",
        "IF最低价",
        "IF成交量",
        "IF成交金额",
        "CSI300收盘价",
        "delta"
      };
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }
      int rowNum = 1;
      for (DiffBetweenIFAndIndexView diff : diffBetweenIFAndIndexViews) {
        Row row = sheet.createRow(rowNum++);
        int i = 0;
        row.createCell(i++).setCellValue(Objects.toString(diff.getDate(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfCode(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getDiff(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfOpenPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfClosePrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfHighPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfLowPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfVolume(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfAmount(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getCsi300ClosePrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getDelta(), ""));
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public List<FutureHistory> getNoSmoothIFFutureHistory(int startYear, Month startMonth) {
    HashMap<LocalDate, List<CFFEXFutureHistory>> stockFutureHistory =
        featureService.getStockFutureHistory(startYear, startMonth);
    List<FutureHistory> result = new ArrayList<>();
    for (List<CFFEXFutureHistory> futureHistoryList : stockFutureHistory.values()) {
      CFFEXFutureHistory mainIFHistory = getMainIFHistory(futureHistoryList);
      if (mainIFHistory == null) {
        continue;
      }
      FutureHistory futureHistory = new FutureHistory(mainIFHistory);
      result.add(futureHistory);
    }
    return result.stream().sorted(Comparator.comparing(FutureHistory::getDate)).toList();
  }

  private CFFEXFutureHistory getMainIFHistory(List<CFFEXFutureHistory> cffexFutureHistoryList) {
    if (cffexFutureHistoryList.isEmpty()) {
      return null;
    }
    CFFEXFutureHistory result = null;
    BigDecimal holdingVolume = BigDecimal.ZERO;
    BigDecimal amount = BigDecimal.ZERO;
    for (CFFEXFutureHistory cffexFutureHistory : cffexFutureHistoryList) {
      if (!cffexFutureHistory.getCode().startsWith("IF")) {
        continue;
      }
      if (cffexFutureHistory.getAmount().compareTo(amount) > 0
          && cffexFutureHistory.getHoldingVolume().compareTo(holdingVolume) > 0) {
        result = cffexFutureHistory;
        amount = cffexFutureHistory.getAmount();
        holdingVolume = cffexFutureHistory.getHoldingVolume();
      }
    }
    return result;
  }
}
