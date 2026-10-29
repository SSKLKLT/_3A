package com.shop._3a.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期展示工具。
 */
public final class FormatUtil {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FormatUtil() {
    }

    public static String dateTime(LocalDateTime time) {
        return time == null ? "" : time.format(DATE_TIME);
    }

    public static String date(LocalDate date) {
        return date == null ? "" : date.format(DATE);
    }
}
