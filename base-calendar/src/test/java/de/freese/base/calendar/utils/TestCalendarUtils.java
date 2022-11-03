package de.freese.base.calendar.utils;

import static de.freese.base.utils.CalendarUtils.calendarToDayOfWeek;
import static de.freese.base.utils.CalendarUtils.dayOfWeekToCalendar;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.freese.base.utils.CalendarUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCalendarUtils
{
    @Test
    void testCalendarAtStartOfDay()
    {
        Calendar calendar = new GregorianCalendar();
        CalendarUtils.calendarAtStartOfDay(calendar);

        assertEquals(0, calendar.get(Calendar.MILLISECOND));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.HOUR));
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    void testCalendarCreate()
    {
        Calendar gregorianCalendar = new GregorianCalendar();
        Calendar calendar = CalendarUtils.calendarCreate();

        assertEquals(gregorianCalendar.get(Calendar.SECOND), calendar.get(Calendar.SECOND));
        assertEquals(gregorianCalendar.get(Calendar.MINUTE), calendar.get(Calendar.MINUTE));
        assertEquals(gregorianCalendar.get(Calendar.HOUR), calendar.get(Calendar.HOUR));
        assertEquals(gregorianCalendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY));

        assertEquals(gregorianCalendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(gregorianCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH), calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        assertEquals(gregorianCalendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(gregorianCalendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.DAY_OF_YEAR));

        assertEquals(gregorianCalendar.get(Calendar.MONTH), calendar.get(Calendar.MONTH));
        assertEquals(gregorianCalendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR));
    }

    @Test
    void testToDateLocalDate()
    {
        Calendar calendarRef = CalendarUtils.calendarCreate();

        Date date = CalendarUtils.toDate(LocalDate.now());
        Calendar calendar = CalendarUtils.calendarCreate(date);

        assertEquals(calendarRef.get(Calendar.YEAR), calendar.get(Calendar.YEAR));
        assertEquals(calendarRef.get(Calendar.MONTH), calendar.get(Calendar.MONTH));

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK_IN_MONTH), calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
    }

    @Test
    void testToDateLocalDateTime()
    {
        Calendar calendarRef = CalendarUtils.calendarCreate();

        Date date = CalendarUtils.toDate(LocalDateTime.now());
        Calendar calendar = CalendarUtils.calendarCreate(date);

        assertEquals(calendarRef.get(Calendar.YEAR), calendar.get(Calendar.YEAR));
        assertEquals(calendarRef.get(Calendar.MONTH), calendar.get(Calendar.MONTH));

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK_IN_MONTH), calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));

        assertEquals(calendarRef.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(calendarRef.get(Calendar.HOUR), calendar.get(Calendar.HOUR));
        assertEquals(calendarRef.get(Calendar.MINUTE), calendar.get(Calendar.MINUTE));
        assertEquals(calendarRef.get(Calendar.SECOND), calendar.get(Calendar.SECOND));
    }

    @Test
    void testToInstantDateSql()
    {
        java.sql.Date dateRef = new java.sql.Date(System.currentTimeMillis());
        Calendar calendarRef = CalendarUtils.calendarCreate(dateRef);

        Instant instant = CalendarUtils.toInstant(dateRef);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        assertEquals(calendarRef.get(Calendar.YEAR), zonedDateTime.getYear());
        assertEquals(calendarRef.get(Calendar.MONTH) + 1, zonedDateTime.getMonth().getValue());

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), zonedDateTime.getDayOfYear());
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), zonedDateTime.getDayOfMonth());
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), dayOfWeekToCalendar(zonedDateTime.getDayOfWeek()));

        assertEquals(calendarRef.get(Calendar.HOUR_OF_DAY), zonedDateTime.getHour());
        assertEquals(calendarRef.get(Calendar.MINUTE), zonedDateTime.getMinute());
        assertEquals(calendarRef.get(Calendar.SECOND), zonedDateTime.getSecond());
    }

    @Test
    void testToInstantDateUtil()
    {
        Date dateRef = new Date();
        Calendar calendarRef = CalendarUtils.calendarCreate(dateRef);

        Instant instant = CalendarUtils.toInstant(dateRef);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        assertEquals(calendarRef.get(Calendar.YEAR), zonedDateTime.getYear());
        assertEquals(calendarRef.get(Calendar.MONTH) + 1, zonedDateTime.getMonth().getValue());

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), zonedDateTime.getDayOfYear());
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), zonedDateTime.getDayOfMonth());
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), dayOfWeekToCalendar(zonedDateTime.getDayOfWeek()));

        assertEquals(calendarRef.get(Calendar.HOUR_OF_DAY), zonedDateTime.getHour());
        assertEquals(calendarRef.get(Calendar.MINUTE), zonedDateTime.getMinute());
        assertEquals(calendarRef.get(Calendar.SECOND), zonedDateTime.getSecond());
    }

    @Test
    void testToInstantLocalDate()
    {
        LocalDate localDateRef = LocalDate.now();

        Instant instant = CalendarUtils.toInstant(localDateRef);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        assertEquals(localDateRef.getYear(), zonedDateTime.getYear());
        assertEquals(localDateRef.getMonth(), zonedDateTime.getMonth());

        assertEquals(localDateRef.getDayOfYear(), zonedDateTime.getDayOfYear());
        assertEquals(localDateRef.getDayOfMonth(), zonedDateTime.getDayOfMonth());
        assertEquals(localDateRef.getDayOfWeek(), zonedDateTime.getDayOfWeek());
    }

    @Test
    void testToInstantLocalDateTime()
    {
        LocalDateTime localDateTimeRef = LocalDateTime.now();

        Instant instant = CalendarUtils.toInstant(localDateTimeRef);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        assertEquals(localDateTimeRef.getYear(), zonedDateTime.getYear());
        assertEquals(localDateTimeRef.getMonth(), zonedDateTime.getMonth());

        assertEquals(localDateTimeRef.getDayOfYear(), zonedDateTime.getDayOfYear());
        assertEquals(localDateTimeRef.getDayOfMonth(), zonedDateTime.getDayOfMonth());
        assertEquals(localDateTimeRef.getDayOfWeek(), zonedDateTime.getDayOfWeek());

        assertEquals(localDateTimeRef.getHour(), zonedDateTime.getHour());
        assertEquals(localDateTimeRef.getMinute(), zonedDateTime.getMinute());
        assertEquals(localDateTimeRef.getSecond(), zonedDateTime.getSecond());
    }

    @Test
    void testToLocalDateDate()
    {
        Date dateRef = new Date();
        Calendar calendarRef = CalendarUtils.calendarCreate(dateRef);

        LocalDate localDate = CalendarUtils.toLocalDate(dateRef);

        assertEquals(calendarRef.get(Calendar.YEAR), localDate.getYear());
        assertEquals(calendarRef.get(Calendar.MONTH) + 1, localDate.getMonth().getValue());

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), localDate.getDayOfYear());
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), localDate.getDayOfMonth());
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), dayOfWeekToCalendar(localDate.getDayOfWeek()));
    }

    @Test
    void testToLocalDateInstant()
    {
        LocalDateTime localDateTimeRef = LocalDateTime.now();

        Instant instant = CalendarUtils.toInstant(localDateTimeRef);
        LocalDate localDate = CalendarUtils.toLocalDate(instant);

        assertEquals(localDateTimeRef.getYear(), localDate.getYear());
        assertEquals(localDateTimeRef.getMonth(), localDate.getMonth());

        assertEquals(localDateTimeRef.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(localDateTimeRef.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(localDateTimeRef.getDayOfWeek(), localDate.getDayOfWeek());
    }

    @Test
    void testToLocalDateLocalDateTime()
    {
        LocalDate localDateRef = LocalDate.now();

        LocalDate localDate = CalendarUtils.toLocalDate(LocalDateTime.now());

        assertEquals(localDateRef.getYear(), localDate.getYear());
        assertEquals(localDateRef.getMonth(), localDate.getMonth());

        assertEquals(localDateRef.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(localDateRef.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(localDateRef.getDayOfWeek(), localDate.getDayOfWeek());
    }

    @Test
    void testToLocalDateTimeDate()
    {
        Date dateRef = new Date();
        Calendar calendarRef = CalendarUtils.calendarCreate(dateRef);

        LocalDateTime localDateTime = CalendarUtils.toLocalDateTime(dateRef);

        assertEquals(calendarRef.get(Calendar.YEAR), localDateTime.getYear());
        assertEquals(calendarRef.get(Calendar.MONTH) + 1, localDateTime.getMonth().getValue());

        assertEquals(calendarRef.get(Calendar.DAY_OF_YEAR), localDateTime.getDayOfYear());
        assertEquals(calendarRef.get(Calendar.DAY_OF_MONTH), localDateTime.getDayOfMonth());
        assertEquals(calendarRef.get(Calendar.DAY_OF_WEEK), dayOfWeekToCalendar(localDateTime.getDayOfWeek()));

        assertEquals(calendarRef.get(Calendar.HOUR_OF_DAY), localDateTime.getHour());
        assertEquals(calendarRef.get(Calendar.MINUTE), localDateTime.getMinute());
        assertEquals(calendarRef.get(Calendar.SECOND), localDateTime.getSecond());
    }

    @Test
    void testToLocalDateTimeInstant()
    {
        LocalDate localDateRef = LocalDate.now();

        Instant instant = CalendarUtils.toInstant(localDateRef);
        LocalDateTime localDateTime = CalendarUtils.toLocalDateTime(instant);

        assertEquals(localDateRef.getYear(), localDateTime.getYear());
        assertEquals(localDateRef.getMonth(), localDateTime.getMonth());

        assertEquals(localDateRef.getDayOfYear(), localDateTime.getDayOfYear());
        assertEquals(localDateRef.getDayOfMonth(), localDateTime.getDayOfMonth());
        assertEquals(localDateRef.getDayOfWeek(), localDateTime.getDayOfWeek());

        assertEquals(0, localDateTime.getHour());
        assertEquals(0, localDateTime.getMinute());
        assertEquals(0, localDateTime.getSecond());
        assertEquals(0, localDateTime.getNano());
    }

    @Test
    void testToLocalDateTimeLocalDate()
    {
        LocalDate localDateRef = LocalDate.now();

        LocalDateTime localDateTime = CalendarUtils.toLocalDateTime(localDateRef);

        assertEquals(localDateRef.getYear(), localDateTime.getYear());
        assertEquals(localDateRef.getMonth(), localDateTime.getMonth());

        assertEquals(localDateRef.getDayOfYear(), localDateTime.getDayOfYear());
        assertEquals(localDateRef.getDayOfMonth(), localDateTime.getDayOfMonth());
        assertEquals(localDateRef.getDayOfWeek(), localDateTime.getDayOfWeek());

        assertEquals(0, localDateTime.getHour());
        assertEquals(0, localDateTime.getMinute());
        assertEquals(0, localDateTime.getSecond());
        assertEquals(0, localDateTime.getNano());
    }

    @Test
    void testToSqlDateInstant()
    {
        LocalDate localDateRef = LocalDate.now();

        Instant instant = CalendarUtils.toInstant(localDateRef);

        java.sql.Date date = CalendarUtils.toSqlDate(instant);
        Calendar calendar = CalendarUtils.calendarCreate(date);

        assertEquals(localDateRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateRef.getDayOfWeek(), calendarToDayOfWeek(calendar));
    }

    @Test
    void testToSqlDateLocalDate()
    {
        LocalDate localDateRef = LocalDate.now();

        java.sql.Date date = CalendarUtils.toSqlDate(localDateRef);
        Calendar calendar = CalendarUtils.calendarCreate(date);

        assertEquals(localDateRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateRef.getDayOfWeek(), calendarToDayOfWeek(calendar));
    }

    @Test
    void testToSqlDateLocalDateTime()
    {
        LocalDateTime localDateTimeRef = LocalDateTime.now();

        java.sql.Date date = CalendarUtils.toSqlDate(localDateTimeRef);
        Calendar calendar = CalendarUtils.calendarCreate(date);

        assertEquals(localDateTimeRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateTimeRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateTimeRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateTimeRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateTimeRef.getDayOfWeek(), calendarToDayOfWeek(calendar));

        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
    }

    @Test
    void testToSqlTimestampInstant()
    {
        LocalDate localDateRef = LocalDate.now();

        Instant instant = CalendarUtils.toInstant(localDateRef);

        Timestamp timestamp = CalendarUtils.toSqlTimestamp(instant);
        Calendar calendar = CalendarUtils.calendarCreate(timestamp);

        assertEquals(localDateRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateRef.getDayOfWeek(), calendarToDayOfWeek(calendar));

        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
    }

    @Test
    void testToSqlTimestampLocalDate()
    {
        LocalDate localDateRef = LocalDate.now();

        Timestamp timestamp = CalendarUtils.toSqlTimestamp(localDateRef);
        Calendar calendar = CalendarUtils.calendarCreate(timestamp);

        assertEquals(localDateRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateRef.getDayOfWeek(), calendarToDayOfWeek(calendar));

        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
    }

    @Test
    void testToSqlTimestampLocalDateTime()
    {
        LocalDateTime localDateTimeRef = LocalDateTime.now();

        Timestamp timestamp = CalendarUtils.toSqlTimestamp(localDateTimeRef);
        Calendar calendar = CalendarUtils.calendarCreate(timestamp);

        assertEquals(localDateTimeRef.getYear(), calendar.get(Calendar.YEAR));
        assertEquals(localDateTimeRef.getMonth().getValue(), calendar.get(Calendar.MONTH) + 1);

        assertEquals(localDateTimeRef.getDayOfYear(), calendar.get(Calendar.DAY_OF_YEAR));
        assertEquals(localDateTimeRef.getDayOfMonth(), calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(localDateTimeRef.getDayOfWeek(), calendarToDayOfWeek(calendar));

        assertEquals(localDateTimeRef.getHour(), calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(localDateTimeRef.getMinute(), calendar.get(Calendar.MINUTE));
        assertEquals(localDateTimeRef.getSecond(), calendar.get(Calendar.SECOND));
    }
}
