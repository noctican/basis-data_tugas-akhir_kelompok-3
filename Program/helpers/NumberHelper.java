package helpers;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberHelper {
    public static String formatNumber(double angka, boolean showCurrency, boolean showDecimal) {
        Locale localeID = Locale.of("id", "ID");
        NumberFormat formatter;

        if (showCurrency) formatter = NumberFormat.getCurrencyInstance(localeID);
        else formatter = NumberFormat.getNumberInstance(localeID);

        if (showDecimal) {
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
        } else {
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
        }

        return formatter.format(angka);
    }
}
