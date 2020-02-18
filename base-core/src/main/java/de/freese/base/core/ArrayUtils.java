/**
 * Created: 02.07.2011
 */

package de.freese.base.core;

import java.util.Arrays;

/**
 * Toolkitklasse fuer dier Arbeit mit Arrays.
 *
 * @author Thomas Freese
 */
public final class ArrayUtils
{
    /**
     * Liefert true, wenn in den ArrayElementen immer nur der Wert enthalten ist.
     * 
     * @param values double[]
     * @param value double
     * @return boolean
     */
    public static boolean containsOnly(final double[] values, final double value)
    {
        for (double value2 : values)
        {
            if (value2 != value)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Füllt das Array mit den Replace-Wert, wenn alle Elemente 0 sind.
     * 
     * @param values double[]
     * @param filler double
     * @return double[]
     */
    public static double[] fillWhenAll0(final double[] values, final double filler)
    {
        boolean zero = true;

        for (double value : values)
        {
            zero = value == 0.0D;

            if (!zero)
            {
                break;
            }
        }

        if (zero)
        {
            Arrays.fill(values, filler);
        }

        return values;
    }
}
