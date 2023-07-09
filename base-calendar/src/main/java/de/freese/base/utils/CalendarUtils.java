// Created: 12.02.2020
package de.freese.base.utils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Thomas Freese
 */
public final class CalendarUtils {
    /**
     * Setzt die Stunden, Minuten, Sekunden und Millisekunden auf 0.
     */
    public static void calendarAtStartOfDay(final Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Liefert einen Gregorianischen Kalender.
     */
    public static Calendar calendarCreate() {
        return calendarCreate(new Date());
    }

    /**
     * Liefert einen Gregorianischen Kalender.<br>
     * Calendar calendar = new GregorianCalendar(Locale.GERMAN);<br>
     */
    public static Calendar calendarCreate(final Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.setTime(date);

        return calendar;
    }

    public static DayOfWeek calendarToDayOfWeek(Calendar calendar) {
        int dow = calendar.get(Calendar.DAY_OF_WEEK);

        return switch (dow) {
            case Calendar.MONDAY -> DayOfWeek.MONDAY;
            case Calendar.TUESDAY -> DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY -> DayOfWeek.THURSDAY;
            case Calendar.FRIDAY -> DayOfWeek.FRIDAY;
            case Calendar.SATURDAY -> DayOfWeek.SATURDAY;
            case Calendar.SUNDAY -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unexpected day of week: " + dow);
        };
    }

    public static int dayOfWeekToCalendar(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Calendar.MONDAY;
            case TUESDAY -> Calendar.TUESDAY;
            case WEDNESDAY -> Calendar.WEDNESDAY;
            case THURSDAY -> Calendar.THURSDAY;
            case FRIDAY -> Calendar.FRIDAY;
            case SATURDAY -> Calendar.SATURDAY;
            case SUNDAY -> Calendar.SUNDAY;
        };
    }

    public static Date toDate(final Instant instant) {
        return Date.from(instant);
    }

    public static Date toDate(final LocalDate localDate) {
        Instant instant = toInstant(localDate);

        return toDate(instant);
    }

    public static Date toDate(final LocalDateTime localDateTime) {
        Instant instant = toInstant(localDateTime);

        return toDate(instant);
    }

    public static long toEpochTimeMillies(final LocalDate localDate) {
        return toEpochTimeMillies(localDate.atStartOfDay());
    }

    public static long toEpochTimeMillies(final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static Instant toInstant(final Date date) {
        if (date instanceof java.sql.Date d) {
            // LocalDate localDate = d.toLocalDate();
            // instant = toInstant(localDate);

            return Instant.ofEpochMilli(d.getTime());
        }

        return date.toInstant();
    }

    /**
     * Da LocalDate keine Zeiten enthält, kommt es hier zu einer Exception:<br>
     * Instant.from(localDate);<br>
     */
    public static Instant toInstant(final LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

        return zonedDateTime.toInstant();
    }

    /**
     * LocalDateTime.toInstant(ZoneOffset.UTC);<br>
     * LocalDateTime.atZone(ZoneId.systemDefault()).toInstant();<br>
     */
    public static Instant toInstant(final LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        return zonedDateTime.toInstant();
    }

    public static LocalDate toLocalDate(final Date date) {
        if (date instanceof java.sql.Date d) {
            return d.toLocalDate();
        }

        Instant instant = toInstant(date);

        return toLocalDate(instant);
    }

    public static LocalDate toLocalDate(final long timestamp) {
        return toLocalDate(Instant.ofEpochMilli(timestamp));
    }

    public static LocalDate toLocalDate(final Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate toLocalDate(final LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    /**
     * toLocalDate(date).atStartOfDay();<br>
     */
    public static LocalDateTime toLocalDateTime(final Date date) {
        if (date instanceof Timestamp ts) {
            return ts.toLocalDateTime();
        }

        Instant instant = toInstant(date);

        return toLocalDateTime(instant);
    }

    public static LocalDateTime toLocalDateTime(final long timestamp) {
        return toLocalDateTime(Instant.ofEpochMilli(timestamp));
    }

    public static LocalDateTime toLocalDateTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * LocalDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();<br>
     */
    public static LocalDateTime toLocalDateTime(final LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static long toNanoOfSecond(final LocalDate localDate) {
        return 0;
        //        return toNanoOfSecond(localDate.atStartOfDay());
    }

    public static long toNanoOfSecond(final LocalDateTime localDateTime) {
        return localDateTime.getNano();
    }

    public static java.sql.Date toSqlDate(final Instant instant) {
        LocalDate localDate = toLocalDate(instant);

        return toSqlDate(localDate);
    }

    public static java.sql.Date toSqlDate(final LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static java.sql.Date toSqlDate(final LocalDateTime localDateTime) {
        LocalDate localDate = toLocalDate(localDateTime);

        return toSqlDate(localDate);
    }

    public static Timestamp toSqlTimestamp(final Instant instant) {
        return Timestamp.from(instant);
    }

    public static Timestamp toSqlTimestamp(final LocalDate localDate) {
        LocalDateTime localDateTime = toLocalDateTime(localDate);

        return toSqlTimestamp(localDateTime);
    }

    public static Timestamp toSqlTimestamp(final LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    static Instant toInstant(final TemporalAccessor accessor) {
        return Instant.from(accessor);
    }

    static LocalDate toLocalDate(final TemporalAccessor accessor) {
        if (accessor instanceof LocalDate ld) {
            return ld;
        }
        else if (accessor instanceof LocalDateTime ldt) {
            return ldt.toLocalDate();
        }

        Instant instant = toInstant(accessor);

        return toLocalDate(instant);
    }

    static LocalDateTime toLocalDateTime(final TemporalAccessor accessor) {
        if (accessor instanceof LocalDateTime ldt) {
            return ldt;
        }

        Instant instant = toInstant(accessor);

        return toLocalDateTime(instant);
    }

    private CalendarUtils() {
        super();
    }
}
