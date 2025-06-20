package dev.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtils는 날짜와 시간 관련 유틸리티 메서드를 제공하는 클래스입니다.
 */
public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");


    /**
     * LocalDateTime 객체를 지정된 형식의 문자열로 변환합니다.
     *
     * @param dateTime LocalDateTime 객체
     * @return String 형식화된 날짜 및 시간 문자열
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            return FORMATTER.format(dateTime);
        }
        return null;
    }
}
