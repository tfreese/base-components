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
     * Liefert einen Gregorianischen Kalender.<br>
     * Calendar calendar = new GregorianCalendar(Locale.GERMAN);<br>
     *
     * @param date {@link Date}
     *
     * @return {@link Calendar}
     */
    public static Calendar calendarCreate(final Date date)
    {
        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
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
        return toDate((TemporalAccessor) localDate);
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Date}
     */
    public static Date toDate(final LocalDateTime localDateTime)
    {
        return toDate((TemporalAccessor) localDateTime);
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link Date}
     */
    public static Date toDate(final TemporalAccessor accessor)
    {
        Instant instant = toInstant(accessor);

        return Date.from(instant);
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
     * Da LocalDate keine Zeiten enthält, kommt es hier zu einer Exception:<br>
     * Instant.from(localDate);<br>
     *
     * @param localDate {@link Date}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final LocalDate localDate)
    {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    /**
     * LocalDateTime.toInstant(ZoneOffset.UTC);<br>
     * LocalDateTime.atZone(ZoneId.systemDefault()).toInstant();<br>
     *
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final LocalDateTime localDateTime)
    {
        return toInstant((TemporalAccessor) localDateTime);
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final TemporalAccessor accessor)
    {
        return Instant.from(accessor);
    }

    /**
     * @param date {@link Date}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final Date date)
    {
        if (date instanceof java.sql.Date d)
        {
            return d.toLocalDate();
        }

        Instant instant = toInstant(date);

        return toLocalDate(instant);
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final Instant instant)
    {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link LocalDate}
     */
    public static LocalDate toLocalDate(final LocalDateTime localDateTime)
    {
        return localDateTime.toLocalDate();
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
        else if (accessor instanceof LocalDateTime ldt)
        {
            return ldt.toLocalDate();
        }

        Instant instant = toInstant(accessor);

        return toLocalDate(instant);
    }

    /**
     * toLocalDate(date).atStartOfDay();<br>
     *
     * @param date {@link Date}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final Date date)
    {
        if (date instanceof java.sql.Timestamp ts)
        {
            return ts.toLocalDateTime();
        }

        Instant instant = toInstant(date);

        return toLocalDateTime(instant);
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final Instant instant)
    {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * LocalDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();<br>
     *
     * @param localDate {@link LocalDate}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final LocalDate localDate)
    {
        return localDate.atStartOfDay();
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final TemporalAccessor accessor)
    {
        if (accessor instanceof LocalDateTime ldt)
        {
            return ldt;
        }

        Instant instant = toInstant(accessor);

        return toLocalDateTime(instant);
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link java.sql.Date}
     */
    public static java.sql.Date toSqlDate(final Instant instant)
    {
        LocalDate localDate = toLocalDate(instant);

        return toSqlDate(localDate);
    }

    /**
     * @param localDate {@link LocalDate}
     *
     * @return {@link java.sql.Date}
     */
    public static java.sql.Date toSqlDate(final LocalDate localDate)
    {
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link Timestamp}
     */
    public static Timestamp toSqlTimestamp(final Instant instant)
    {
        return Timestamp.from(instant);
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Timestamp}
     */
    public static Timestamp toSqlTimestamp(final LocalDateTime localDateTime)
    {
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * Erstellt ein neues {@link CalendarUtils} Object.
     */
    private CalendarUtils()
    {
        super();
    }
}
