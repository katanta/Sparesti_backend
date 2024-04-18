package org.ntnu.idi.idatt2106.sparesti.sparestibackend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApplicationUtil {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    public static final RoundingMode roundingMode = RoundingMode.HALF_UP;

    public static BigDecimal percentage(BigDecimal quot, BigDecimal divisor) {
        return quot.divide(divisor, roundingMode).multiply(ONE_HUNDRED);
    }
}
