package de.freese.base.calendar.feiertag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import de.freese.base.utils.CalendarUtils;

/**
 * Liefert die Feiertage fuer ein bestimmtes Datum.
 *
 * @author Thomas Freese
 */
public final class FeiertagsManager
{
    /**
     *
     */
    private final Map<Integer, Map<String, InternerFeiertag>> feiertagsMap = new TreeMap<>();

    /**
     * @param jahr int
     * @param monat int
     * @param tag int
     * @param typ {@link FeiertagTyp}
     * @param berechneterFeiertag boolean
     */
    private final void addFeiertag(final int jahr, final int monat, final int tag, final FeiertagTyp typ, final boolean berechneterFeiertag)
    {
        Map<String, InternerFeiertag> jahrMap = this.feiertagsMap.computeIfAbsent(jahr, key -> new HashMap<>());

        jahrMap.put(monat + "-" + tag, new InternerFeiertag(jahr, monat, tag, typ, berechneterFeiertag));
    }

    /**
     * Einfuegen von Ostern abhaengigen Feiertagen. Geklaut aus CT' (Gauss-Algorithmus).
     *
     * @param jahr int
     */
    private final void erzeugeDynamischeFeiertage(final int jahr)
    {
        int gz;

        int jhd;

        int ksj;
        int korr;

        int so;

        int epakte;

        int n;
        gz = (jahr % 19) + 1;
        jhd = (jahr / 100) + 1;
        ksj = ((3 * jhd) / 4) - 12;
        korr = (((8 * jhd) + 5) / 25) - 5;
        so = ((5 * jahr) / 4) - ksj - 10;
        epakte = (((11 * gz) + 20 + korr) - ksj) % 30;

        if (((epakte == 25) && (gz > 11)) || (epakte == 24))
        {
            epakte++;
        }

        n = 44 - epakte;

        if (n < 21)
        {
            n = n + 30;
        }

        n = (n + 7) - ((so + n) % 7);
        n += ((((jahr % 4) == 0) && (((jahr % 100) != 0) || ((jahr % 400) == 0))) ? 1 : 0);

        int osterTag = n + 59;

        addFeiertag(jahr, getMonatImJahr(jahr, osterTag), getTagDesMonats(jahr, osterTag), FeiertagTyp.OSTER_SONNTAG, true);
        addFeiertag(jahr, getMonatImJahr(jahr, osterTag - 2), getTagDesMonats(jahr, osterTag - 2), FeiertagTyp.KARFREITAG, true);
        addFeiertag(jahr, getMonatImJahr(jahr, osterTag + 1), getTagDesMonats(jahr, osterTag + 1), FeiertagTyp.OSTER_MONTAG, true);
        addFeiertag(jahr, getMonatImJahr(jahr, osterTag + 39), getTagDesMonats(jahr, osterTag + 39), FeiertagTyp.CHRISTI_HIMMELFAHRT, true);
        addFeiertag(jahr, getMonatImJahr(jahr, osterTag + 49), getTagDesMonats(jahr, osterTag + 49), FeiertagTyp.PFINGST_SONNTAG, true);
        addFeiertag(jahr, getMonatImJahr(jahr, osterTag + 50), getTagDesMonats(jahr, osterTag + 50), FeiertagTyp.PFINGST_MONTAG, true);

        // // Nicht in allen Bundeslaendern
        // addFeiertag(
        // jahr, getMonatImJahr(jahr, osterTag + 60), getTagDesMonats(jahr, osterTag + 60),
        // FeiertagTyp.FRONLEICHNAM, true
        // );
    }

    /**
     * Erzeuge alle Feiertage des Jahres, wenn noch nicht vorhanden.
     *
     * @param jahr int
     */
    private final void erzeugeFeiertage(final int jahr)
    {
        // Ist das Jahr schon enthalten?
        if (this.feiertagsMap.containsKey(jahr))
        {
            return;
        }

        erzeugeFesteFeiertage(jahr);
        erzeugeDynamischeFeiertage(jahr);
    }

    /**
     * Einfuegen von festen Feiertagen.
     *
     * @param jahr int
     */
    private final void erzeugeFesteFeiertage(final int jahr)
    {
        addFeiertag(jahr, 1, 1, FeiertagTyp.NEUJAHR, false);
        addFeiertag(jahr, 5, 1, FeiertagTyp.MAIFEIERTAG, false);
        addFeiertag(jahr, 10, 3, FeiertagTyp.TAG_DER_DEUTSCHEN_EINHEIT, false);
        addFeiertag(jahr, 10, 31, FeiertagTyp.REFORMATIONSTAG, false);

        // addFeiertag(jahr, 12, 24, HEILIGABEND,false);
        addFeiertag(jahr, 12, 25, FeiertagTyp.WEIHNACHTEN_1, false);
        addFeiertag(jahr, 12, 26, FeiertagTyp.WEIHNACHTEN_2, false);

        // addFeiertag(jahr, 12, 31, SYLVESTER,false);
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param calendar {@link Calendar}
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final Calendar calendar)
    {
        Feiertag feiertag = getFeiertag(calendar.getTime());

        return feiertag;
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param date {@link Date}
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final Date date)
    {
        LocalDate localDate = CalendarUtils.toLocalDate(date);

        Feiertag feiertag = getFeiertag(localDate);

        return feiertag;
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param jahr int
     * @param tagDesJahres int; 1-366
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final int jahr, final int tagDesJahres)
    {
        LocalDate localDate = LocalDate.ofYearDay(jahr, tagDesJahres);

        Feiertag feiertag = getFeiertag(localDate);

        return feiertag;
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param jahr int
     * @param monat int; 1-12
     * @param tag int; 1-31
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final int jahr, final int monat, final int tag)
    {
        erzeugeFeiertage(jahr);

        InternerFeiertag internerFeiertag = this.feiertagsMap.getOrDefault(jahr, Collections.emptyMap()).get(monat + "-" + tag);
        Feiertag feiertag = null;

        if (internerFeiertag != null)
        {
            feiertag = new Feiertag(internerFeiertag.getTyp(), internerFeiertag.isVariablerFeiertag());
        }

        return feiertag;
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param localDate {@link LocalDate}
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final LocalDate localDate)
    {
        Feiertag feiertag = getFeiertag(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());

        return feiertag;
    }

    /**
     * Liefert einen Feiertag fuer ein bestimmtes Datum.
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link Feiertag}
     */
    public final Feiertag getFeiertag(final LocalDateTime localDateTime)
    {
        Feiertag feiertag = getFeiertag(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth());

        return feiertag;
    }

    /**
     * Liefert den Monat des Tages im Jahr.<br>
     * Eingabe: Jahr Tagesnummer rel. zum Jahresanfang (1=1.1.,2=2.1.,...365/366=31.12)<br>
     * Ausgabe: Wochentag (0=So, 1=Mo,..., 6=Sa)<br>
     * Algorithmus von Zeller, aus CT 15/98
     *
     * @param jahr int
     * @param tagDesJahres int
     * @return int
     */
    private final int getMonatImJahr(final int jahr, final int tagDesJahres)
    {
        int tdj = tagDesJahres;

        int a = 0;

        if (isSchaltJahr(jahr))
        {
            a += 1;
        }

        if (tdj > (59 + a))
        {
            tdj += (2 - a);
        }

        tdj += 91;

        return ((20 * tdj) / 611) - 2;
    }

    /**
     * Eingabe: Jahr Tagesnummer rel. zum Jahresanfang (1=1.1.,2=2.1.,...365/366=31.12)<br>
     * Ausgabe: Tag (1..31)<br>
     * Algorithmus von R. A. Stone, aus CT 15/98
     *
     * @param jahr int
     * @param tagDesJahres int
     * @return int
     */
    private final int getTagDesMonats(final int jahr, final int tagDesJahres)
    {
        int dtj = tagDesJahres;

        int a = 0;
        int m;

        if (isSchaltJahr(jahr))
        {
            a += 1;
        }

        if (dtj > (59 + a))
        {
            dtj += (2 - a);
        }

        dtj += 91;
        m = (20 * dtj) / 611;

        return dtj - ((611 * m) / 20);
    }

    /**
     * @param jahr int
     * @return boolean
     */
    private final boolean isSchaltJahr(final int jahr)
    {
        return ((jahr % 4) == 0) && (((jahr % 100) != 0) || ((jahr % 400) == 0));
    }
}
