/**
 * Created: 03.07.2011
 */

package de.freese.base.core.zodiac;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testklasse fuer die Sternzeichen.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestZodiac
{
    /**
     * Erstellt ein neues {@link TestZodiac} Object.
     */
    public TestZodiac()
    {
        super();
    }

    /**
     * Wassermann, 21.01. - 19.02.
     */
    @Test
    public void testAquarius()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.AQUARIUS.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.AQUARIUS.equals(zodiac));
    }

    /**
     * Widder, 21.03. - 20.04.
     */
    @Test
    public void testAries()
    {
        LocalDate localDate = LocalDate.now().withMonth(Month.MARCH.getValue()).withDayOfMonth(21);
        Zodiac zodiac = Zodiac.getZodiac(localDate);
        assertTrue(Zodiac.ARIES.equals(zodiac));

        localDate = localDate.withMonth(Month.APRIL.getValue()).withDayOfMonth(20);
        zodiac = Zodiac.getZodiac(localDate);
        assertTrue(Zodiac.ARIES.equals(zodiac));
    }

    /**
     * Krebs, 22.06. - 22.07.
     */
    @Test
    public void testCancer()
    {
        LocalDateTime localDateTime = LocalDateTime.now().withMonth(Month.JUNE.getValue()).withDayOfMonth(22);
        Zodiac zodiac = Zodiac.getZodiac(localDateTime);
        assertTrue(Zodiac.CANCER.equals(zodiac));

        localDateTime = localDateTime.withMonth(Month.JULY.getValue()).withDayOfMonth(22);
        zodiac = Zodiac.getZodiac(localDateTime);
        assertTrue(Zodiac.CANCER.equals(zodiac));
    }

    /**
     * Steinbock, 22.12. - 20.01.
     */
    @Test
    public void testCapricorn()
    {
        Zodiac zodiac = Zodiac.getZodiac(Month.DECEMBER.getValue(), 22);
        assertTrue(Zodiac.CAPRICORN.equals(zodiac));

        zodiac = Zodiac.getZodiac(Month.JANUARY.getValue(), 20);
        assertTrue(Zodiac.CAPRICORN.equals(zodiac));
    }

    /**
     * Zwillinge, 21.05. - 21.06.
     */
    @Test
    public void testGemini()
    {
        Zodiac zodiac = Zodiac.getZodiac(5, 21);
        assertTrue(Zodiac.GEMINI.equals(zodiac));

        zodiac = Zodiac.getZodiac(6, 21);
        assertTrue(Zodiac.GEMINI.equals(zodiac));
    }

    /**
     * Loewe, 23.07. - 23.08.
     */
    @Test
    public void testLeo()
    {
        Zodiac zodiac = Zodiac.getZodiac(7, 23);
        assertTrue(Zodiac.LEO.equals(zodiac));

        zodiac = Zodiac.getZodiac(8, 23);
        assertTrue(Zodiac.LEO.equals(zodiac));
    }

    /**
     * Waage, 24.09. - 23.10.
     */
    @Test
    public void testLibra()
    {
        Zodiac zodiac = Zodiac.getZodiac(9, 24);
        assertTrue(Zodiac.LIBRA.equals(zodiac));

        zodiac = Zodiac.getZodiac(10, 23);
        assertTrue(Zodiac.LIBRA.equals(zodiac));
    }

    /**
     * Fische, 20.02. - 20.03.
     */
    @Test
    public void testPisces()
    {
        Zodiac zodiac = Zodiac.getZodiac(2, 20);
        assertTrue(Zodiac.PISCES.equals(zodiac));

        zodiac = Zodiac.getZodiac(3, 20);
        assertTrue(Zodiac.PISCES.equals(zodiac));
    }

    /**
     * Schuetze, 23.11. - 21.12.
     */
    @Test
    public void testSagittarius()
    {
        Zodiac zodiac = Zodiac.getZodiac(11, 23);
        assertTrue(Zodiac.SAGITTARIUS.equals(zodiac));

        zodiac = Zodiac.getZodiac(12, 21);
        assertTrue(Zodiac.SAGITTARIUS.equals(zodiac));
    }

    /**
     * Skorpion, 24.10. - 22.11.
     */
    @Test
    public void testScorpio()
    {
        Zodiac zodiac = Zodiac.getZodiac(10, 24);
        assertTrue(Zodiac.SCORPIO.equals(zodiac));

        zodiac = Zodiac.getZodiac(11, 22);
        assertTrue(Zodiac.SCORPIO.equals(zodiac));
    }

    /**
     * Stier, 21.04. - 20.05.
     */
    @Test
    public void testTaurus()
    {
        Zodiac zodiac = Zodiac.getZodiac(4, 21);
        assertTrue(Zodiac.TAURUS.equals(zodiac));

        zodiac = Zodiac.getZodiac(5, 20);
        assertTrue(Zodiac.TAURUS.equals(zodiac));
    }

    /**
     * Jungfrau, 24.08. - 23.09.
     */
    @Test
    public void testVirgo()
    {
        Zodiac zodiac = Zodiac.getZodiac(8, 24);
        assertTrue(Zodiac.VIRGO.equals(zodiac));

        zodiac = Zodiac.getZodiac(9, 23);
        assertTrue(Zodiac.VIRGO.equals(zodiac));
    }
}
