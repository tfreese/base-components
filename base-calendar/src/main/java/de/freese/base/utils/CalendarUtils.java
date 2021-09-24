// Created: 12.02.2020
package de.freese.base.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Thomas Freese
 */
public final class CalendarUtils
{
    /**
     * Setzt die Stunden, Minuten, Sekunden und Millisekunden auf 0.
     *
     * @param calendar {@link Calendar}
     */
    public static void calendarAtStartOfDay(final Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Liefert einen Gregorianischen Kalender.
     *
     * @return {@link Calendar}
     */
    public static Calendar calendarCreate()
    {
        return calendarCreate(new Date());
    }

    /**
     * Liefert einen Gregorianischen Kalender.
     *
     * @param date {@link Date}
     *
     * @return {@link Calendar}
     */
    public static Calendar calendarCreate(final Date date)
    {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        // Calendar calendar = new GregorianCalendar(Locale.GERMAN);
        calendar.setTime(date);

        return calendar;
    }

    /**
     * @param localDate {@link LocalDate}
     *
     * @return {@link Date}
     */
    public static Date toDate(final LocalDate localDate)
    {
        Instant instant = toInstant(localDate);

        Date date = Date.from(instant);
        // date = toDate(toLocalDateTime(localDate));

        return date;
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Date}
     */
    public static Date toDate(final LocalDateTime localDateTime)
    {
        Instant instant = toInstant(localDateTime);

        Date date = Date.from(instant);

        return date;
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link Date}
     */
    public static Date toDate(final TemporalAccessor accessor)
    {
        Instant instant = toInstant(accessor);

        Date date = Date.from(instant);
        // date = toDate(toLocalDateTime(localDate));

        return date;
    }

    /**
     * @param date {@link Date}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final Date date)
    {
        Instant instant = null;

        if (date instanceof java.sql.Date d)
        {
            LocalDate localDate = d.toLocalDate();

            instant = toInstant(localDate);
        }
        else
        {
            instant = date.toInstant();
        }

        return instant;
    }

    /**
     * @param localDate {@link Date}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final LocalDate localDate)
    {
        // Da LocalDate keine Zeiten enthält, kommt es hier zu einer Exception.
        // Instant instant = Instant.from(localDate);

        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        return instant;
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final LocalDateTime localDateTime)
    {
        Instant instant = Instant.from(localDateTime);
        // instant = localDateTime.toInstant(ZoneOffset.UTC);
        // instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return instant;
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final TemporalAccessor accessor)
    {
        Instant instant = Instant.from(accessor);

        return instant;
    }

    /**
     * @param date {@link Date}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final Date date)
    {
        Instant instant = toInstant(date);

        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        return localDate;
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final LocalDateTime localDateTime)
    {
        LocalDate localDate = localDateTime.toLocalDate();

        return localDate;
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final TemporalAccessor accessor)
    {
        if (accessor instanceof LocalDate ld)
        {
            return ld;
        }

        Instant instant = toInstant(accessor);

        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        return localDate;
    }

    /**
     * @param date {@link Date}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final Date date)
    {
        Instant instant = toInstant(date);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        // localDateTime = toLocalDate(date).atStartOfDay();

        return localDateTime;
    }

    /**
     * @param localDate {@link LocalDate}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final LocalDate localDate)
    {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        // localDateTime = localDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();

        return localDateTime;
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final TemporalAccessor accessor)
    {
        if (accessor instanceof LocalDate)
        {
            return (LocalDateTime) accessor;
        }

        Instant instant = toInstant(accessor);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return localDateTime;
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link java.sql.Date}
     */
    public static java.sql.Date toSqlDate(final Instant instant)
    {
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        return sqlDate;
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link Timestamp}
     */
    public static Timestamp toTimestamp(final Instant instant)
    {
        Timestamp timestamp = Timestamp.from(instant);

        return timestamp;
    }

    /**
     * Erstellt ein neues {@link CalendarUtils} Object.
     */
    private CalendarUtils()
    {
        super();
    }
}
