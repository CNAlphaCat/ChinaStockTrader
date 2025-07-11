package cn.alphacat.chinastocktrader.service.future;

import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineTypeEnum;
import cn.alphacat.chinastockdata.enums.EastMoneyQTKlineWeightingEnum;
import cn.alphacat.chinastockdata.future.FutureService;
import cn.alphacat.chinastockdata.model.future.CFFEXFutureHistory;
import cn.alphacat.chinastockdata.model.future.FutureHistory;
import cn.alphacat.chinastockdata.model.marketindex.MarketIndex;
import cn.alphacat.chinastocktrader.model.future.IMHistory;
import cn.alphacat.chinastocktrader.service.marketindex.CSI1000IndexService;
import cn.alphacat.chinastocktrader.util.LocalDateUtil;
import cn.alphacat.chinastocktrader.view.future.DiffBetweenIMAndIndexView;
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
public class IMFutureService {
  private final FutureService featureService;
  private final CSI1000IndexService csi1000IndexService;

  private static final String IM_MAIN_FUTURE_CODE = "8.150130";
  private static final String IM = "IM";

  public IMFutureService(
      final FutureService featureService, final CSI1000IndexService csi1000IndexService) {
    this.featureService = featureService;
    this.csi1000IndexService = csi1000IndexService;
  }

  public List<FutureHistory> getFutureHistory(
      LocalDate beginDate,
      LocalDate endDate,
      EastMoneyQTKlineTypeEnum klt,
      EastMoneyQTKlineWeightingEnum fqt) {
    return featureService.getFutureHistory(IM_MAIN_FUTURE_CODE, beginDate, endDate, klt, fqt);
  }

  public List<FutureHistory> getTodayFutureHistory() {
    return featureService.getFutureHistory(
        IM_MAIN_FUTURE_CODE,
        LocalDateUtil.getNow(),
        LocalDateUtil.getNow(),
        EastMoneyQTKlineTypeEnum.ONE_MINUTE,
        EastMoneyQTKlineWeightingEnum.NON_WEIGHTING);
  }

  public List<DiffBetweenIMAndIndexView> getDiffBetweenIMAndIndex(int startYear, Month startMonth) {
    List<IMHistory> noSmoothIMFutureHistory = getNoSmoothIMFutureHistory(startYear, startMonth);
    Map<LocalDate, IMHistory> noSmoothIMFutureHistoryMap =
        noSmoothIMFutureHistory.stream()
            .collect(Collectors.toMap(IMHistory::getDate, futureHistory -> futureHistory));
    LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
    List<MarketIndex> csi1000IndexDaily = csi1000IndexService.getCSI1000IndexDaily(startDate);
    Map<LocalDate, MarketIndex> csi1000IndexDailyMap =
        csi1000IndexDaily.stream()
            .collect(Collectors.toMap(MarketIndex::getTradeDate, marketIndex -> marketIndex));
    List<DiffBetweenIMAndIndexView> result = new ArrayList<>();
    for (LocalDate date : csi1000IndexDailyMap.keySet()) {
      IMHistory futureHistory = noSmoothIMFutureHistoryMap.get(date);
      if (futureHistory == null) {
        continue;
      }
      MarketIndex csi1000Index = csi1000IndexDailyMap.get(date);
      if (csi1000Index == null) {
        continue;
      }
      DiffBetweenIMAndIndexView diffBetweenIMAndIndexView =
          getDiffBetweenIMAndIndexView(
              date, futureHistory.getMain(), futureHistory.getRecentlyMonth(), csi1000Index);
      result.add(diffBetweenIMAndIndexView);
    }
    return result.stream()
        .sorted(Comparator.comparing(DiffBetweenIMAndIndexView::getDate))
        .toList();
  }

  public byte[] exportDiffToExcel(int startYear, Month startMonth) throws IOException {
    List<DiffBetweenIMAndIndexView> diffBetweenIMAndIndexViews =
        getDiffBetweenIMAndIndex(startYear, startMonth);

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("IM与指数点差");
      Row headerRow = sheet.createRow(0);
      String[] headers = {"日期", "价差", "IM", "CSI1000", "IM品种"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }
      int rowNum = 1;
      for (DiffBetweenIMAndIndexView view : diffBetweenIMAndIndexViews) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(view.getDate().toString());
        row.createCell(1).setCellValue(view.getMainDiff().doubleValue());
        row.createCell(2).setCellValue(view.getImMainClosePrice().doubleValue());
        row.createCell(3).setCellValue(view.getCsi1000ClosePrice().doubleValue());
        row.createCell(4).setCellValue(view.getImMainCode());
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public List<IMHistory> getNoSmoothIMFutureHistory(int startYear, Month startMonth) {
    HashMap<LocalDate, List<CFFEXFutureHistory>> stockFutureHistory =
        featureService.getStockFutureHistory(startYear, startMonth);
    List<IMHistory> result = new ArrayList<>();
    for (List<CFFEXFutureHistory> futureHistoryList : stockFutureHistory.values()) {
      CFFEXFutureHistory mainIFHistory = getMainIMHistory(futureHistoryList);
      CFFEXFutureHistory recentlyMonthIFHistory = getRecentIMHistory(futureHistoryList);

      FutureHistory main = new FutureHistory(mainIFHistory);
      FutureHistory recentlyMonth = new FutureHistory(recentlyMonthIFHistory);
      IMHistory imHistory = new IMHistory();
      imHistory.setMain(main);
      imHistory.setRecentlyMonth(recentlyMonth);
      imHistory.setDate(main.getDate());
      result.add(imHistory);
    }
    return result.stream().sorted(Comparator.comparing(IMHistory::getDate)).toList();
  }

  private static DiffBetweenIMAndIndexView getDiffBetweenIMAndIndexView(
      LocalDate date, FutureHistory main, FutureHistory recentlyMonth, MarketIndex csi1000Index) {
    DiffBetweenIMAndIndexView diffBetweenIMAndIndexView = new DiffBetweenIMAndIndexView();

    BigDecimal imMainClose = main.getClose();
    BigDecimal csi1000IndexClose = csi1000Index.getClose();

    diffBetweenIMAndIndexView.setDate(date);

    diffBetweenIMAndIndexView.setMainDiff(imMainClose.subtract(csi1000IndexClose));
    diffBetweenIMAndIndexView.setImMainCode(main.getCode());
    diffBetweenIMAndIndexView.setImMainOpenPrice(main.getOpen());
    diffBetweenIMAndIndexView.setImMainClosePrice(imMainClose);
    diffBetweenIMAndIndexView.setImMainHighPrice(main.getHigh());
    diffBetweenIMAndIndexView.setImMainLowPrice(main.getLow());
    diffBetweenIMAndIndexView.setImMainVolume(main.getVolume());
    diffBetweenIMAndIndexView.setImMainAmount(main.getAmount());

    diffBetweenIMAndIndexView.setRecentlyMonthDiff(
        recentlyMonth.getClose().subtract(csi1000IndexClose));
    diffBetweenIMAndIndexView.setImRecentlyMonthCode(recentlyMonth.getCode());
    diffBetweenIMAndIndexView.setImRecentlyMonthOpenPrice(recentlyMonth.getOpen());
    diffBetweenIMAndIndexView.setImRecentlyMonthClosePrice(recentlyMonth.getClose());
    diffBetweenIMAndIndexView.setImRecentlyMonthHighPrice(recentlyMonth.getHigh());
    diffBetweenIMAndIndexView.setImRecentlyMonthLowPrice(recentlyMonth.getLow());
    diffBetweenIMAndIndexView.setImRecentlyMonthVolume(recentlyMonth.getVolume());
    diffBetweenIMAndIndexView.setImRecentlyMonthAmount(recentlyMonth.getAmount());

    diffBetweenIMAndIndexView.setCsi1000ClosePrice(csi1000IndexClose);

    return diffBetweenIMAndIndexView;
  }

  private CFFEXFutureHistory getMainIMHistory(List<CFFEXFutureHistory> cffexFutureHistoryList) {
    if (cffexFutureHistoryList.isEmpty()) {
      return null;
    }
    CFFEXFutureHistory result = null;
    BigDecimal holdingVolume = BigDecimal.ZERO;
    BigDecimal amount = BigDecimal.ZERO;
    for (CFFEXFutureHistory cffexFutureHistory : cffexFutureHistoryList) {
      if (!cffexFutureHistory.getCode().startsWith(IM)) {
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

  private CFFEXFutureHistory getRecentIMHistory(List<CFFEXFutureHistory> cffexFutureHistoryList) {
    if (cffexFutureHistoryList.isEmpty()) {
      return null;
    }

    for (CFFEXFutureHistory cffexFutureHistory : cffexFutureHistoryList) {
      if (!cffexFutureHistory.getCode().startsWith(IM)) {
        continue;
      }
      LocalDate date = cffexFutureHistory.getDate();
      int currentYear = date.getYear();
      int currentMonth = date.getMonthValue();
      String currentYearString = String.valueOf(currentYear).substring(2);
      String currentMonthString =
          currentMonth < 10 ? "0" + currentMonth : String.valueOf(currentMonth);
      String currentCode = IM + currentYearString + currentMonthString;
      if (currentCode.equals(cffexFutureHistory.getCode().trim())
          && cffexFutureHistory.getHoldingVolume().compareTo(BigDecimal.ZERO) > 0) {
        return cffexFutureHistory;
      }

      LocalDate nextMonthDate = date.plusMonths(1);
      int year = nextMonthDate.getYear();
      int month = nextMonthDate.getMonthValue();

      String yearString = String.valueOf(year).substring(2);
      String monthString = month < 10 ? "0" + month : String.valueOf(month);
      String code = IM + yearString + monthString;

      if (code.equals(cffexFutureHistory.getCode().trim())) {
        return cffexFutureHistory;
      }
    }
    return null;
  }
}
