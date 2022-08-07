// Created: 12.02.2020
package de.freese.base.utils;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
     * @param calendar {@link Calendar}
     *
     * @return DayOfWeek
     */
    public static DayOfWeek calendarToDayOfWeek(Calendar calendar)
    {
        int dow = calendar.get(Calendar.DAY_OF_WEEK);

        return switch (dow)
                {
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

    /**
     * @param dayOfWeek {@link DayOfWeek}
     *
     * @return int
     */
    public static int dayOfWeekToCalendar(DayOfWeek dayOfWeek)
    {
        return switch (dayOfWeek)
                {
                    case MONDAY -> Calendar.MONDAY;
                    case TUESDAY -> Calendar.TUESDAY;
                    case WEDNESDAY -> Calendar.WEDNESDAY;
                    case THURSDAY -> Calendar.THURSDAY;
                    case FRIDAY -> Calendar.FRIDAY;
                    case SATURDAY -> Calendar.SATURDAY;
                    case SUNDAY -> Calendar.SUNDAY;
                };
    }

    /**
     * @param instant {@link Instant}
     *
     * @return {@link Date}
     */
    public static Date toDate(final Instant instant)
    {
        return Date.from(instant);
    }

    /**
     * @param localDate {@link LocalDate}
     *
     * @return {@link Date}
     */
    public static Date toDate(final LocalDate localDate)
    {
        Instant instant = toInstant(localDate);

        return toDate(instant);
    }

    /**
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link Date}
     */
    public static Date toDate(final LocalDateTime localDateTime)
    {
        Instant instant = toInstant(localDateTime);

        return toDate(instant);
    }

    /**
     * @param date {@link Date}
     *
     * @return {@link Instant}
     */
    public static Instant toInstant(final Date date)
    {
        if (date instanceof java.sql.Date d)
        {
            // LocalDate localDate = d.toLocalDate();
            // instant = toInstant(localDate);

            return Instant.ofEpochMilli(d.getTime());
        }

        return date.toInstant();
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
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

        return zonedDateTime.toInstant();
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
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        return zonedDateTime.toInstant();
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
     * toLocalDate(date).atStartOfDay();<br>
     *
     * @param date {@link Date}
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(final Date date)
    {
        if (date instanceof Timestamp ts)
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
     * @param localDateTime {@link LocalDateTime}
     *
     * @return {@link java.sql.Date}
     */
    public static java.sql.Date toSqlDate(final LocalDateTime localDateTime)
    {
        LocalDate localDate = toLocalDate(localDateTime);

        return toSqlDate(localDate);
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
     * @param localDate {@link LocalDate}
     *
     * @return {@link Timestamp}
     */
    public static Timestamp toSqlTimestamp(final LocalDate localDate)
    {
        LocalDateTime localDateTime = toLocalDateTime(localDate);

        return toSqlTimestamp(localDateTime);
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
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link Instant}
     */
    static Instant toInstant(final TemporalAccessor accessor)
    {
        return Instant.from(accessor);
    }

    /**
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link LocalDate}
     */
    static LocalDate toLocalDate(final TemporalAccessor accessor)
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
     * @param accessor {@link TemporalAccessor}
     *
     * @return {@link LocalDateTime}
     */
    static LocalDateTime toLocalDateTime(final TemporalAccessor accessor)
    {
        if (accessor instanceof LocalDateTime ldt)
        {
            return ldt;
        }

        Instant instant = toInstant(accessor);

        return toLocalDateTime(instant);
    }

    /**
     * Erstellt ein neues {@link CalendarUtils} Object.
     */
    private CalendarUtils()
    {
        super();
    }
}
