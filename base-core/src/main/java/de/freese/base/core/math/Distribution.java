package de.freese.base.core.math;

import java.util.Arrays;

/**
 * Verschiedene Methoden zur Distribution von Daten anhand Faktoren usw.
 *
 * @author Thomas Freese
 */
public final class Distribution
{
    /**
     * Teilt den Wert durch die gewünschte Anzahl.
     */
    public static double[] linear(final int anzahl, final double wert)
    {
        return linear(anzahl, wert, 0);
    }

    /**
     * Teilt den Wert durch die gewünschte Anzahl.
     */
    public static double[] linear(final int anzahl, final double wert, final int nachkommaStellen)
    {
        // Lineare Distribution -> alle Faktoren auf 1
        double[] faktoren = new double[anzahl];
        Arrays.fill(faktoren, 1.0D);

        return proportional(faktoren, wert, nachkommaStellen);
    }

    /**
     * Verteilt den Wert anhand der Faktoren auf die einzelnen Elemente ohne Nachkommastelle.<br>
     * Sind ALLE Faktoren null, so wird linear verteilt.
     */
    public static double[] proportional(final double[] faktoren, final double wert)
    {
        return proportional(faktoren, wert, 0);
    }

    /**
     * Verteilt den Wert anhand der Faktoren auf die einzelnen Elemente mit Nachkommastellen.<br>
     * Sind ALLE Faktoren null, so wird linear verteilt.
     */
    public static double[] proportional(final double[] faktoren, final double wert, final int nachkommaStellen)
    {
        double mWert = wert;
        double[] daten = new double[faktoren.length];
        Arrays.fill(daten, 0.0D);

        if (Double.isNaN(mWert) || Double.isInfinite(mWert) || (Double.compare(mWert, 0.0D) == 0))
        {
            return daten;
        }

        double faktorSumme = 0.0D;

        for (double element : faktoren)
        {
            double faktor = element;

            if (Double.isNaN(faktor) || Double.isInfinite(faktor))
            {
                faktor = 0.0D;
            }

            faktorSumme += faktor;
        }

        for (int i = 0; i < faktoren.length; i++)
        {
            if (Double.compare(faktorSumme, 0.0D) == 0)
            {
                break; //  Ohne dieses Abbrechen kann NaN eingetragen werden!
            }

            double faktor = faktoren[i];

            if (Double.isNaN(faktor) || Double.isInfinite(faktor))
            {
                faktor = 0.0D;
            }

            daten[i] = ExtMath.round((mWert / faktorSumme) * faktor, nachkommaStellen);

            mWert -= daten[i];
            faktorSumme -= faktor;
        }

        return daten;
    }

    /**
     * Verteilt den Wert anhand der Faktoren auf die einzelnen Elemente ohne Nachkommastellen.<br>
     * Sind ALLE Faktoren null, so wird linear verteilt.
     */
    public static double[] proportional(final Double[] faktoren, final double wert)
    {
        double[] faktorenDouble = new double[faktoren.length];

        for (int i = 0; i < faktoren.length; i++)
        {
            if (faktoren[i] == null)
            {
                continue;
            }

            faktorenDouble[i] = faktoren[i];
        }

        return proportional(faktorenDouble, wert, 0);
    }

    private Distribution()
    {
        super();
    }
}
