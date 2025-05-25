package com.leaf.collegeidleapp.util;

import java.math.BigDecimal; // 使用标准BigDecimal类

public class NumberUtils {
    public static BigDecimal parsePaymentAmount(String amountStr) {
        try {
            // 使用旧版ROUND_HALF_UP常量（int类型）
            return new BigDecimal(amountStr.replaceAll(",", ""))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}