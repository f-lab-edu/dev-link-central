package dev.linkcentral.service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            return FORMATTER.format(dateTime);
        }
        return null;
    }
}
