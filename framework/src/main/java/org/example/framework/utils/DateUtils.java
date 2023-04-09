package org.example.framework.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
public class DateUtils {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    private static DateTimeFormatter dateDayFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate parseDate(String dateText) {
        return LocalDate.parse(dateText, dateFormatter);
    }

    public static LocalDateTime parseDateTime(String dateText, Boolean maxDateTime) {
        if(dateText.length() == 10) {
            if(maxDateTime) {
                dateText += " 23:59:59";
            } else {
                dateText += " 00:00:00";
            }
        }
        return LocalDateTime.parse(dateText, dateTimeFormatter);
    }

    public static String formatDateMonth(LocalDateTime dateTime) {
        return dateMonthFormatter.format(dateTime);
    }

    public static String formatDateDay(LocalDateTime dateTime) {
        return dateDayFormatter.format(dateTime);
    }

    public static String formatDateDay(LocalDate dateTime) {
        return dateDayFormatter.format(dateTime);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime);
    }


    public static LocalDate parseDateDay(String dateText) {
        return LocalDate.parse(dateText, dateDayFormatter);
    }
}
