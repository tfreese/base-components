// Created: 02.07.2011
package de.freese.base.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Thomas Freese
 */
public final class ArrayUtils {
    public static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * Liefert true, wenn in den ArrayElementen immer nur der Wert enthalten ist.
     *
     * @return boolean
     */
    public static boolean containsOnly(final double[] values, final double value) {
        for (double value2 : values) {
            if (Double.compare(value, value2) != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Füllt das Array mit dem Replace-Wert, wenn alle Elemente 0 sind.
     */
    public static double[] fillWhenAll0(final double[] values, final double filler) {
        boolean zero = true;

        for (double value : values) {
            zero = Double.compare(value, 0.0D) == 0;

            if (!zero) {
                break;
            }
        }

        if (zero) {
            Arrays.fill(values, filler);
        }

        return values;
    }

    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(final double[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(final int[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(final long[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(final Object[] array) {
        return !isEmpty(array);
    }

    public static String join(final Object[] array, final String separator) {
        if (isEmpty(array)) {
            return "";
        }

        return Stream.of(array).map(Object::toString).collect(Collectors.joining(separator));
    }

    private ArrayUtils() {
        super();
    }
}
