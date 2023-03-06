package com.example.smsweb.utils;

import java.time.LocalDate;

public class FormatDate {
    public static String dateFormat(LocalDate value, String format) {
        String date = "";
        String day = value.getDayOfMonth() < 10 ? "0" + value.getDayOfMonth() : String.valueOf(value.getDayOfMonth());
        String month = value.getMonthValue() < 10 ? "0" + value.getMonthValue() : String.valueOf(value.getMonthValue());
        String year = value.getYear() < 10 ? "0" + value.getYear() : String.valueOf(value.getYear());
        switch (format.toLowerCase()) {
            case "dd/mm/yyyy":
                date = day + "/" + month + "/" + year;
                break;
            case "mm/dd/yyyy":
                date = month + "/" + day + "/" + year;
                break;
            case "yyyy/mm/dd":
                date = year + "/" + month + "/" + day;
                break;
            case "dd-mm-yyyy":
                date = day + "-" + month + "-" + year;
                break;
            case "mm-dd-yyy":
                date = month + "-" + day + "-" + year;
                break;
            case "yyyy-mm-dd":
                date = year + "-" + month + "-" + day;
                break;
        }
        return date;
    }
}
