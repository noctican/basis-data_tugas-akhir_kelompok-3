package helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberHelper {
    public static String formatNumber(Number angka, boolean showCurrency, boolean showDecimal) {
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

    public static String formatNumber(Number angka) {
        Locale localeID = Locale.of("id", "ID");
        NumberFormat formatter;

        formatter = NumberFormat.getCurrencyInstance(localeID);

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(angka);
    }



    public static double parseRupiahToDouble(String rupiahString) {
        if (rupiahString == null || rupiahString.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            String cleanString = rupiahString.replace("Rp", "")
                                             .replace(" ", "")
                                             .replace(".", "");
                                             
            cleanString = cleanString.replace(",", ".");
            
            return Double.parseDouble(cleanString);
            
        } catch (NumberFormatException e) {
            System.out.println("Gagal mengubah format angka: " + e.getMessage());
            return 0.0; 
        }
    }

    public static BigDecimal parseRupiahToBigDecimal(String rupiahString) {
        return BigDecimal.valueOf(parseRupiahToDouble(rupiahString));
    }

    public static void setRupiah(JTable tabel, int indexKolom) {
        tabel.getColumnModel().getColumn(indexKolom).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) value = formatNumber((Number) value);
                super.setValue(value);
            }
        });
    }
}