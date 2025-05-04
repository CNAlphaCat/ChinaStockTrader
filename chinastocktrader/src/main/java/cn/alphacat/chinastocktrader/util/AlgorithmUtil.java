package cn.alphacat.chinastocktrader.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class AlgorithmUtil {
  private static final BigDecimal EPSILON = new BigDecimal("0.1");
  private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

  public static BigDecimal getStockLimitSentimentScore(int upInteger, int downInteger) {
    BigDecimal up = new BigDecimal(upInteger);
    BigDecimal down = new BigDecimal(downInteger);
    BigDecimal numerator = up.add(EPSILON);
    BigDecimal denominator = down.add(EPSILON);

    BigDecimal division = numerator.divide(denominator, MATH_CONTEXT);
    return BigDecimalUtil.log(division);
  }
}
