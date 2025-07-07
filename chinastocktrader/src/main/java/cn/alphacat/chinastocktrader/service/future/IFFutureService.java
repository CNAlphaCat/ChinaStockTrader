package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureMarketOverview;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.model.future.IFHistory;
import cn.alphacat.chinastocktrader.service.marketindex.CSI300IndexService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.future.DiffBetweenIFAndIndexView;
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
    List<IFHistory> noSmoothIMFutureHistory = getNoSmoothIFFutureHistory(startYear, startMonth);
    Map<LocalDate, IFHistory> noSmoothIMFutureHistoryMap =
        noSmoothIMFutureHistory.stream()
            .collect(Collectors.toMap(IFHistory::getDate, futureHistory -> futureHistory));
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    List<MarketIndex> csi300IndexDaily = csi300IndexService.getCSI300IndexDaily(startDate);
    Map<LocalDate, MarketIndex> csi300IndexDailyMap =
        csi300IndexDaily.stream()
            .collect(Collectors.toMap(MarketIndex::getTradeDate, marketIndex -> marketIndex));
    List<DiffBetweenIFAndIndexView> result = new ArrayList<>();
    for (LocalDate date : csi300IndexDailyMap.keySet()) {
      IFHistory futureHistory = noSmoothIMFutureHistoryMap.get(date);
      if (futureHistory == null) {
        continue;
      }
      MarketIndex csi300Index = csi300IndexDailyMap.get(date);
      if (csi300Index == null) {
        continue;
      }
      DiffBetweenIFAndIndexView diffBetweenIFAndIndexView =
          getDiffBetweenIFAndIndexView(
              date, futureHistory.getMain(), futureHistory.getRecentlyMonth(), csi300Index);

      result.add(diffBetweenIFAndIndexView);
    }
    return result.stream()
        .sorted(Comparator.comparing(DiffBetweenIFAndIndexView::getDate))
        .toList();
  }

  private static DiffBetweenIFAndIndexView getDiffBetweenIFAndIndexView(
      LocalDate date, FutureHistory main, FutureHistory nextMonth, MarketIndex csi300Index) {
    DiffBetweenIFAndIndexView diffBetweenIFAndIndexView = new DiffBetweenIFAndIndexView();

    BigDecimal ifmainClose = main.getClose();
    BigDecimal csi300IndexClose = csi300Index.getClose();

    diffBetweenIFAndIndexView.setDate(date);
    diffBetweenIFAndIndexView.setMainDiff(ifmainClose.subtract(csi300IndexClose));
    diffBetweenIFAndIndexView.setIfMainCode(main.getCode());
    diffBetweenIFAndIndexView.setIfMainOpenPrice(main.getOpen());
    diffBetweenIFAndIndexView.setIfMainClosePrice(ifmainClose);
    diffBetweenIFAndIndexView.setIfMainHighPrice(main.getHigh());
    diffBetweenIFAndIndexView.setIfMainLowPrice(main.getLow());
    diffBetweenIFAndIndexView.setIfMainVolume(main.getVolume());
    diffBetweenIFAndIndexView.setIfMainAmount(main.getAmount());

    diffBetweenIFAndIndexView.setRecentlyMonthDiff(nextMonth.getClose().subtract(csi300IndexClose));
    diffBetweenIFAndIndexView.setIfRecentlyMonthCode(nextMonth.getCode());
    diffBetweenIFAndIndexView.setIfRecentlyMonthOpenPrice(nextMonth.getOpen());
    diffBetweenIFAndIndexView.setIfRecentlyMonthClosePrice(nextMonth.getClose());
    diffBetweenIFAndIndexView.setIfRecentlyMonthHighPrice(nextMonth.getHigh());
    diffBetweenIFAndIndexView.setIfRecentlyMonthLowPrice(nextMonth.getLow());
    diffBetweenIFAndIndexView.setIfRecentlyMonthVolume(nextMonth.getVolume());
    diffBetweenIFAndIndexView.setIfRecentlyMonthAmount(nextMonth.getAmount());

    diffBetweenIFAndIndexView.setCsi300ClosePrice(csi300IndexClose);

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
        "IF主连品种",
        "IF主连与指数收盘价价差",
        "IF主连开盘价",
        "IF主连收盘价",
        "IF主连最高价",
        "IF主连最低价",
        "IF主连成交量",
        "IF主连成交金额",
        "IF次月品种",
        "IF次月与指数收盘价价差",
        "IF次月开盘价",
        "IF次月收盘价",
        "IF次月最高价",
        "IF次月最低价",
        "IF次月成交量",
        "IF次月成交金额",
        "CSI300收盘价"
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

        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainCode(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getMainDiff(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainOpenPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainClosePrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainHighPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainLowPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainVolume(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfMainAmount(), ""));

        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthCode(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getRecentlyMonthDiff(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthOpenPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthClosePrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthHighPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthLowPrice(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthVolume(), ""));
        row.createCell(i++).setCellValue(Objects.toString(diff.getIfRecentlyMonthAmount(), ""));

        row.createCell(i++).setCellValue(Objects.toString(diff.getCsi300ClosePrice(), ""));
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public List<IFHistory> getNoSmoothIFFutureHistory(int startYear, Month startMonth) {
    HashMap<LocalDate, List<CFFEXFutureHistory>> stockFutureHistory =
        featureService.getStockFutureHistory(startYear, startMonth);
    List<IFHistory> result = new ArrayList<>();
    for (List<CFFEXFutureHistory> futureHistoryList : stockFutureHistory.values()) {
      CFFEXFutureHistory mainIFHistory = getMainIFHistory(futureHistoryList);
      CFFEXFutureHistory recentlyMonthIFHistory = getRecentIFHistory(futureHistoryList);

      FutureHistory main = new FutureHistory(mainIFHistory);
      FutureHistory recentlyMonth = new FutureHistory(recentlyMonthIFHistory);

      IFHistory ifHistory = new IFHistory();
      ifHistory.setMain(main);
      ifHistory.setRecentlyMonth(recentlyMonth);
      ifHistory.setDate(main.getDate());

      result.add(ifHistory);
    }
    return result.stream().sorted(Comparator.comparing(IFHistory::getDate)).toList();
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

  private CFFEXFutureHistory getRecentIFHistory(List<CFFEXFutureHistory> cffexFutureHistoryList) {
    if (cffexFutureHistoryList.isEmpty()) {
      return null;
    }
    CFFEXFutureHistory result = null;
    for (CFFEXFutureHistory cffexFutureHistory : cffexFutureHistoryList) {
      if (!cffexFutureHistory.getCode().startsWith("IF")) {
        continue;
      }
      LocalDate date = cffexFutureHistory.getDate();
      int currentYear = date.getYear();
      int currentMonth = date.getMonthValue();
      String currentYearString = String.valueOf(currentYear).substring(2);
      String currentMonthString =
          currentMonth < 10 ? "0" + currentMonth : String.valueOf(currentMonth);
      String currentCode = "IF" + currentYearString + currentMonthString;
      if (currentCode.equals(cffexFutureHistory.getCode())
          && cffexFutureHistory.getAmount().compareTo(BigDecimal.ZERO) > 0) {
        result = cffexFutureHistory;
        break;
      }

      LocalDate nextMonthDate = date.plusMonths(1);
      int year = nextMonthDate.getYear();
      int month = nextMonthDate.getMonthValue();

      String yearString = String.valueOf(year).substring(2);
      String monthString = month < 10 ? "0" + month : String.valueOf(month);
      String code = "IF" + yearString + monthString;

      String recentlyMonth = cffexFutureHistory.getCode().trim();
      if (code.equals(recentlyMonth)) {
        result = cffexFutureHistory;
        break;
      }
    }
    return result;
  }
}
