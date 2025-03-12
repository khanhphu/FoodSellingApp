package com.example.foodsellingapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Format {
    public static String formatVND(long price) {
        // Custom format with Vietnamese style
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Use dot as thousand separator

        DecimalFormat vndFormat = new DecimalFormat("#,##0", symbols); // No decimals for long
        return vndFormat.format(price) + " VND"; // Add VND symbol
    }
}
