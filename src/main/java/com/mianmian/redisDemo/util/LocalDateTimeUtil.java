package com.mianmian.redisDemo.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.Optional;

public final class LocalDateTimeUtil {
    public static final LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    public static final LocalDateTime MAX_DATE_TIME = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

    private LocalDateTimeUtil() {
    }

    public static String getDateTimeAsString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    public static String getDateTimeAsString(LocalDateTime localDateTime) {
        return getDateTimeAsString(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime getLocalDateTimeOrDefault(LocalDateTime localDateTime, LocalDateTime defaultLocalDateTime) {
        return (LocalDateTime) Optional.ofNullable(localDateTime).orElse(defaultLocalDateTime);
    }

    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    public static String getDateTimeStringOfTimestamp(Long timestamp) {
        LocalDateTime localDateTime = getDateTimeOfTimestamp(timestamp);
        return getDateTimeAsString(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDate yearMonthToLocalDate(YearMonth yearMonth) {
        return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
    }

    public static YearMonth localDateToYearMonth(LocalDate localDate) {
        return YearMonth.of(localDate.getYear(), localDate.getMonthValue());
    }

    public static LocalDateTime withTimeAtStartOfDay(LocalDateTime localDateTime) {
        return localDateTime.with(LocalTime.MIN);
    }

    public static LocalDateTime withTimeAtEndOfDay(LocalDateTime localDateTime) {
        return localDateTime.with(LocalTime.MAX);
    }

    public static LocalDateTime minDateTimeOfDay() {
        return LocalDateTime.now().with(LocalTime.MIN);
    }

    public static LocalDateTime maxDateTimeOfDay() {
        return LocalDateTime.now().with(LocalTime.MAX);
    }

    public static LocalDateTime monthStartTime(LocalDateTime month) {
        return month.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
    }

    public static LocalDateTime monthEndTime(LocalDateTime month) {
        return month.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    public static LocalDateTime findThisWeekday(DayOfWeek dayOfWeek) {
        if (Objects.isNull(dayOfWeek)) {
            return null;
        } else {
            LocalDateTime now = LocalDate.now().atStartOfDay();
            return now.getDayOfWeek().getValue() < dayOfWeek.getValue() ? now.with(TemporalAdjusters.nextOrSame(dayOfWeek)) : now.with(TemporalAdjusters.previousOrSame(dayOfWeek));
        }
    }
}

