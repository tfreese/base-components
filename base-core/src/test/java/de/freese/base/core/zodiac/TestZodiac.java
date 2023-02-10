// Created: 03.07.2011
package de.freese.base.core.zodiac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Testklasse für die Sternzeichen.
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestZodiac {
    /**
     * Wassermann, 21.01. - 19.02.
     */
    @Test
    void testAquarius() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertEquals(Zodiac.AQUARIUS, zodiac);

        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertEquals(Zodiac.AQUARIUS, zodiac);
    }

    /**
     * Widder, 21.03. - 20.04.
     */
    @Test
    void testAries() {
        LocalDate localDate = LocalDate.now().withMonth(Month.MARCH.getValue()).withDayOfMonth(21);
        Zodiac zodiac = Zodiac.getZodiac(localDate);
        assertEquals(Zodiac.ARIES, zodiac);

        localDate = localDate.withMonth(Month.APRIL.getValue()).withDayOfMonth(20);
        zodiac = Zodiac.getZodiac(localDate);
        assertEquals(Zodiac.ARIES, zodiac);
    }

    /**
     * Krebs, 22.06. - 22.07.
     */
    @Test
    void testCancer() {
        LocalDateTime localDateTime = LocalDateTime.now().withMonth(Month.JUNE.getValue()).withDayOfMonth(22);
        Zodiac zodiac = Zodiac.getZodiac(localDateTime);
        assertEquals(Zodiac.CANCER, zodiac);

        localDateTime = localDateTime.withMonth(Month.JULY.getValue()).withDayOfMonth(22);
        zodiac = Zodiac.getZodiac(localDateTime);
        assertEquals(Zodiac.CANCER, zodiac);
    }

    /**
     * Steinbock, 22.12. - 20.01.
     */
    @Test
    void testCapricorn() {
        Zodiac zodiac = Zodiac.getZodiac(Month.DECEMBER.getValue(), 22);
        assertEquals(Zodiac.CAPRICORN, zodiac);

        zodiac = Zodiac.getZodiac(Month.JANUARY.getValue(), 20);
        assertEquals(Zodiac.CAPRICORN, zodiac);
    }

    /**
     * Zwillinge, 21.05. - 21.06.
     */
    @Test
    void testGemini() {
        Zodiac zodiac = Zodiac.getZodiac(5, 21);
        assertEquals(Zodiac.GEMINI, zodiac);

        zodiac = Zodiac.getZodiac(6, 21);
        assertEquals(Zodiac.GEMINI, zodiac);
    }

    /**
     * Loewe, 23.07. - 23.08.
     */
    @Test
    void testLeo() {
        Zodiac zodiac = Zodiac.getZodiac(7, 23);
        assertEquals(Zodiac.LEO, zodiac);

        zodiac = Zodiac.getZodiac(8, 23);
        assertEquals(Zodiac.LEO, zodiac);
    }

    /**
     * Waage, 24.09. - 23.10.
     */
    @Test
    void testLibra() {
        Zodiac zodiac = Zodiac.getZodiac(9, 24);
        assertEquals(Zodiac.LIBRA, zodiac);

        zodiac = Zodiac.getZodiac(10, 23);
        assertEquals(Zodiac.LIBRA, zodiac);
    }

    /**
     * Fische, 20.02. - 20.03.
     */
    @Test
    void testPisces() {
        Zodiac zodiac = Zodiac.getZodiac(2, 20);
        assertEquals(Zodiac.PISCES, zodiac);

        zodiac = Zodiac.getZodiac(3, 20);
        assertEquals(Zodiac.PISCES, zodiac);
    }

    /**
     * Schütze, 23.11. - 21.12.
     */
    @Test
    void testSagittarius() {
        Zodiac zodiac = Zodiac.getZodiac(11, 23);
        assertEquals(Zodiac.SAGITTARIUS, zodiac);

        zodiac = Zodiac.getZodiac(12, 21);
        assertEquals(Zodiac.SAGITTARIUS, zodiac);
    }

    /**
     * Skorpion, 24.10. - 22.11.
     */
    @Test
    void testScorpio() {
        Zodiac zodiac = Zodiac.getZodiac(10, 24);
        assertEquals(Zodiac.SCORPIO, zodiac);

        zodiac = Zodiac.getZodiac(11, 22);
        assertEquals(Zodiac.SCORPIO, zodiac);
    }

    /**
     * Stier, 21.04. - 20.05.
     */
    @Test
    void testTaurus() {
        Zodiac zodiac = Zodiac.getZodiac(4, 21);
        assertEquals(Zodiac.TAURUS, zodiac);

        zodiac = Zodiac.getZodiac(5, 20);
        assertEquals(Zodiac.TAURUS, zodiac);
    }

    /**
     * Jungfrau, 24.08. - 23.09.
     */
    @Test
    void testVirgo() {
        Zodiac zodiac = Zodiac.getZodiac(8, 24);
        assertEquals(Zodiac.VIRGO, zodiac);

        zodiac = Zodiac.getZodiac(9, 23);
        assertEquals(Zodiac.VIRGO, zodiac);
    }
}
