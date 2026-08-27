package de.freese.base.core.zodiac;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Testklasse für die Sternzeichen.
 *
 * @author Thomas Freese
 * @since 03.07.2011
 */
@Execution(ExecutionMode.CONCURRENT)
class TestZodiac {
    /**
     * Wassermann, 21.01. - 19.02.
     */
    @Test
    void testAquarius() {
        Zodiac zodiac = Zodiac.getZodiac(LocalDate.now().withMonth(Month.JANUARY.getValue()).withDayOfMonth(21));
        assertEquals(Zodiac.AQUARIUS, zodiac);

        zodiac = Zodiac.getZodiac(LocalDateTime.now().withMonth(Month.FEBRUARY.getValue()).withDayOfMonth(19));
        assertEquals(Zodiac.AQUARIUS, zodiac);
    }

    /**
     * Widder, 21.03. - 20.04.
     */
    @Test
    void testAries() {
        Zodiac zodiac = Zodiac.getZodiac(Month.MARCH, 21);
        assertEquals(Zodiac.ARIES, zodiac);

        zodiac = Zodiac.getZodiac(Month.APRIL, 20);
        assertEquals(Zodiac.ARIES, zodiac);
    }

    /**
     * Krebs, 22.06. - 22.07.
     */
    @Test
    void testCancer() {
        Zodiac zodiac = Zodiac.getZodiac(Month.JUNE, 22);
        assertEquals(Zodiac.CANCER, zodiac);

        zodiac = Zodiac.getZodiac(Month.JULY, 22);
        assertEquals(Zodiac.CANCER, zodiac);
    }

    /**
     * Steinbock, 22.12. - 20.01.
     */
    @Test
    void testCapricorn() {
        Zodiac zodiac = Zodiac.getZodiac(Month.DECEMBER, 22);
        assertEquals(Zodiac.CAPRICORN, zodiac);

        zodiac = Zodiac.getZodiac(Month.JANUARY, 20);
        assertEquals(Zodiac.CAPRICORN, zodiac);
    }

    /**
     * Zwillinge, 21.05. - 21.06.
     */
    @Test
    void testGemini() {
        Zodiac zodiac = Zodiac.getZodiac(Month.MAY, 21);
        assertEquals(Zodiac.GEMINI, zodiac);

        zodiac = Zodiac.getZodiac(Month.JUNE, 21);
        assertEquals(Zodiac.GEMINI, zodiac);
    }

    /**
     * Loewe, 23.07. - 23.08.
     */
    @Test
    void testLeo() {
        Zodiac zodiac = Zodiac.getZodiac(Month.JULY, 23);
        assertEquals(Zodiac.LEO, zodiac);

        zodiac = Zodiac.getZodiac(Month.AUGUST, 23);
        assertEquals(Zodiac.LEO, zodiac);
    }

    /**
     * Waage, 24.09. - 23.10.
     */
    @Test
    void testLibra() {
        Zodiac zodiac = Zodiac.getZodiac(Month.SEPTEMBER, 24);
        assertEquals(Zodiac.LIBRA, zodiac);

        zodiac = Zodiac.getZodiac(Month.OCTOBER, 23);
        assertEquals(Zodiac.LIBRA, zodiac);
    }

    /**
     * Fische, 20.02. - 20.03.
     */
    @Test
    void testPisces() {
        Zodiac zodiac = Zodiac.getZodiac(Month.FEBRUARY, 20);
        assertEquals(Zodiac.PISCES, zodiac);

        zodiac = Zodiac.getZodiac(Month.MARCH, 20);
        assertEquals(Zodiac.PISCES, zodiac);
    }

    /**
     * Schütze, 23.11. - 21.12.
     */
    @Test
    void testSagittarius() {
        Zodiac zodiac = Zodiac.getZodiac(Month.NOVEMBER, 23);
        assertEquals(Zodiac.SAGITTARIUS, zodiac);

        zodiac = Zodiac.getZodiac(Month.DECEMBER, 21);
        assertEquals(Zodiac.SAGITTARIUS, zodiac);
    }

    /**
     * Skorpion, 24.10. - 22.11.
     */
    @Test
    void testScorpio() {
        Zodiac zodiac = Zodiac.getZodiac(Month.OCTOBER, 24);
        assertEquals(Zodiac.SCORPIO, zodiac);

        zodiac = Zodiac.getZodiac(Month.NOVEMBER, 22);
        assertEquals(Zodiac.SCORPIO, zodiac);
    }

    /**
     * Stier, 21.04. - 20.05.
     */
    @Test
    void testTaurus() {
        Zodiac zodiac = Zodiac.getZodiac(Month.APRIL, 21);
        assertEquals(Zodiac.TAURUS, zodiac);

        zodiac = Zodiac.getZodiac(Month.MAY, 20);
        assertEquals(Zodiac.TAURUS, zodiac);
    }

    /**
     * Jungfrau, 24.08. - 23.09.
     */
    @Test
    void testVirgo() {
        Zodiac zodiac = Zodiac.getZodiac(Month.AUGUST, 24);
        assertEquals(Zodiac.VIRGO, zodiac);

        zodiac = Zodiac.getZodiac(Month.SEPTEMBER, 23);
        assertEquals(Zodiac.VIRGO, zodiac);
    }
}
