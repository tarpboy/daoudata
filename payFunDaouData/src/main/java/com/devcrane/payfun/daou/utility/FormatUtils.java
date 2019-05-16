package com.devcrane.payfun.daou.utility;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ntb on 6/2/16.
 */
public class FormatUtils {

    public static Double parseDouble(String money) {
        try {
            String replace = money.trim().replace(",", "");
            return Double.valueOf(replace);
        } catch (Exception ex) {
            return Double.valueOf(0);
        }
    }

    public static String formatMoney(double value) {
        try {
            return NumberFormat.getInstance(Locale.US).format(value);
        } catch (Exception ex) {
            return "0";
        }
    }
}
