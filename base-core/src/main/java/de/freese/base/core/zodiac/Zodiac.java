package de.freese.base.core.zodiac;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Thomas Freese
 */
public enum Zodiac {
    /**
     * Wassermann<br>
     * 21.01.
     */
    AQUARIUS(121, 219),
    /**
     * Widder<br>
     * 21.03.
     */
    ARIES(321, 420),
    /**
     * Krebs<br>
     * 22.06.
     */
    CANCER(622, 722),
    /**
     * Steinbock<br>
     * 22.12.
     */
    CAPRICORN(1222, 120),
    /**
     * Zwillinge<br>
     * 21.05.
     */
    GEMINI(521, 621),
    /**
     * Loewe<br>
     * 23.07.
     */
    LEO(723, 823),
    /**
     * Waage<br>
     * 24.09.
     */
    LIBRA(924, 1023),
    /**
     * Fische<br>
     * 20.02.
     */
    PISCES(220, 320),
    /**
     * Schütze<br>
     * 23.11
     */
    SAGITTARIUS(1123, 1221),
    /**
     * Skorpion<br>
     * 24.10.
     */
    SCORPIO(1024, 1122),
    /**
     * Stier<br>
     * 21.04.
     */
    TAURUS(421, 520),
    /**
     * Jungfrau<br>
     * 24.08.
     */
    VIRGO(824, 923);

    /**
     * Map mit Anfang: (M)MDD
     */
    private static final TreeMap<Integer, Zodiac> ZODIAC_MAP = new TreeMap<>();

    static {
        ZODIAC_MAP.put(Zodiac.AQUARIUS.getStart(), Zodiac.AQUARIUS);
        ZODIAC_MAP.put(Zodiac.PISCES.getStart(), Zodiac.PISCES);
        ZODIAC_MAP.put(Zodiac.ARIES.getStart(), Zodiac.ARIES);
        ZODIAC_MAP.put(Zodiac.TAURUS.getStart(), Zodiac.TAURUS);
        ZODIAC_MAP.put(Zodiac.GEMINI.getStart(), Zodiac.GEMINI);
        ZODIAC_MAP.put(Zodiac.CANCER.getStart(), Zodiac.CANCER);
        ZODIAC_MAP.put(Zodiac.LEO.getStart(), Zodiac.LEO);
        ZODIAC_MAP.put(Zodiac.VIRGO.getStart(), Zodiac.VIRGO);
        ZODIAC_MAP.put(Zodiac.LIBRA.getStart(), Zodiac.LIBRA);
        ZODIAC_MAP.put(Zodiac.SCORPIO.getStart(), Zodiac.SCORPIO);
        ZODIAC_MAP.put(Zodiac.SAGITTARIUS.getStart(), Zodiac.SAGITTARIUS);
        ZODIAC_MAP.put(Zodiac.CAPRICORN.getStart(), Zodiac.CAPRICORN);
    }

    public static Zodiac getZodiac(final Date date) {
        final int monat = Integer.parseInt(String.format("%1$tm", date));
        final int tag = Integer.parseInt(String.format("%1$td", date));

        return getZodiac(monat, tag);
    }

    public static Zodiac getZodiac(final int month, final int dayOfMonth) {
        final Integer monatTag = Integer.valueOf(month + "" + dayOfMonth);

        Entry<Integer, Zodiac> entry = ZODIAC_MAP.floorEntry(monatTag);

        if (entry == null) {
            // Jahreswechsel beim Steinbock
            entry = ZODIAC_MAP.lastEntry();
        }

        return entry.getValue();
    }

    public static Zodiac getZodiac(final LocalDate localDate) {
        return getZodiac(localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    public static Zodiac getZodiac(final LocalDateTime localDateTime) {
        return getZodiac(localDateTime.getMonthValue(), localDateTime.getDayOfMonth());
    }

    /**
     * Enthält das Ende des Sternzeichens.<br>
     * Format: (M)MDD, inklusiv dieses Tages
     */
    private final int end;

    /**
     * Enthält den Begin des Sternzeichens.<br>
     * Format: (M)MDD
     */
    private final int start;

    Zodiac(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Liefert das Ende des Sternzeichens.<br>
     *
     * @return int, Format: (M)MDD, inklusiv dieses Tages
     */
    int getEnd() {
        return end;
    }

    /**
     * Liefert den Begin des Sternzeichens.<br>
     *
     * @return int, Format: (M)MDD
     */
    int getStart() {
        return start;
    }
}
