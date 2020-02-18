/**
 * Created: 03.07.2011
 */

package de.freese.base.core.zodiac;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.ARIES.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.ARIES.equals(zodiac));
    }

    /**
     * Krebs, 22.06. - 22.07.
     */
    @Test
    public void testCancer()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.CANCER.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.CANCER.equals(zodiac));
    }

    /**
     * Steinbock, 22.12. - 20.01.
     */
    @Test
    public void testCapricorn()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.CAPRICORN.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.CAPRICORN.equals(zodiac));
    }

    /**
     * Zwillinge, 21.05. - 21.06.
     */
    @Test
    public void testGemini()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.GEMINI.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.GEMINI.equals(zodiac));
    }

    /**
     * Loewe, 23.07. - 23.08.
     */
    @Test
    public void testLeo()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.LEO.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.LEO.equals(zodiac));
    }

    /**
     * Waage, 24.09. - 23.10.
     */
    @Test
    public void testLibra()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.LIBRA.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.LIBRA.equals(zodiac));
    }

    /**
     * Fische, 20.02. - 20.03.
     */
    @Test
    public void testPisces()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.PISCES.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.PISCES.equals(zodiac));
    }

    /**
     * Schuetze, 23.11. - 21.12.
     */
    @Test
    public void testSagittarius()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.SAGITTARIUS.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.SAGITTARIUS.equals(zodiac));
    }

    /**
     * Skorpion, 24.10. - 22.11.
     */
    @Test
    public void testScorpio()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.SCORPIO.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.SCORPIO.equals(zodiac));
    }

    /**
     * Stier, 21.04. - 20.05.
     */
    @Test
    public void testTaurus()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.TAURUS.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.TAURUS.equals(zodiac));
    }

    /**
     * Jungfrau, 24.08. - 23.09.
     */
    @Test
    public void testVirgo()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        Zodiac zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.VIRGO.equals(zodiac));

        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        zodiac = Zodiac.getZodiac(calendar.getTime());
        assertTrue(Zodiac.VIRGO.equals(zodiac));
    }
}
