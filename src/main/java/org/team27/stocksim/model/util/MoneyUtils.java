package org.team27.stocksim.model.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class MoneyUtils {
    public static BigDecimal money(String amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        try {
            return (BigDecimal) decimalFormat.parse(amount);

        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse money amount: " + amount, e);
        }
    }
}
