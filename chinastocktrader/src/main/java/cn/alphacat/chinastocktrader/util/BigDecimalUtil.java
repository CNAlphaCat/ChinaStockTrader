package cn.alphacat.chinastocktrader.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalUtil {
  private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

  public static BigDecimal log(BigDecimal x) {
    int iterations = 100;
    BigDecimal result = BigDecimal.ZERO;
    BigDecimal term = x.subtract(BigDecimal.ONE);
    BigDecimal denominator = x.add(BigDecimal.ONE);
    BigDecimal ratio = term.divide(denominator, MATH_CONTEXT);

    BigDecimal squaredRatio = ratio.multiply(ratio);
    BigDecimal currentTerm = ratio;

    for (int i = 1; i <= iterations; i += 2) {
      result = result.add(currentTerm.divide(new BigDecimal(i), MATH_CONTEXT));
      currentTerm = currentTerm.multiply(squaredRatio);
    }
    return result.multiply(new BigDecimal(2));
  }
}
