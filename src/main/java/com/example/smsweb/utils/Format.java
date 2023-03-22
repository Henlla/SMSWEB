package com.example.smsweb.utils;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Format {
    public static String dateFormat(String date, String format) {
        String dateFormat = "";
        DateTimeFormatter formatDate;
        switch (format.toLowerCase()) {
            case "dd/mm/yyyy":
                formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
            case "mm/dd/yyyy":
                formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
            case "yyyy/mm/dd":
                formatDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
            case "dd-mm-yyyy":
                formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
            case "mm-dd-yyy":
                formatDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
            case "yyyy-mm-dd":
                formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dateFormat = LocalDate.parse(date).format(formatDate);
                break;
        }
        return dateFormat;
    }

    public static LocalTime timeFormat(String time) {
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(LocalTime.parse(time).format(formatTime));
    }
}
