// Created: 29.03.2020
package de.freese.base.core.throttle.google;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The following tests were adapted directly from com.google.common.math.LongMathTest
 *
 * @author Louis Wasserman - Original LongMathTest author
 * @author James P Edwards
 */
class LongMathTest
{
    /**
     *
     */
    private static final List<Long> ALL_LONG_CANDIDATES = new ArrayList<>();
    /**
     *
     */
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    /**
     *
     */
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    static
    {
        ALL_LONG_CANDIDATES.add((long) (Integer.MAX_VALUE - 1));
        ALL_LONG_CANDIDATES.add((long) Integer.MAX_VALUE);

        for (long i = 1; i <= 40; i++)
        {
            ALL_LONG_CANDIDATES.add(i);
        }

        ALL_LONG_CANDIDATES.add(Integer.MAX_VALUE + 1L);
        ALL_LONG_CANDIDATES.add(Long.MAX_VALUE - 1L);
        ALL_LONG_CANDIDATES.add(Long.MAX_VALUE);

        for (final int exponent : new int[]
        {
                2, 3, 4, 9, 15, 16, 17, 24, 25, 30, 32, 33, 39, 40, 41, 47, 48, 49, 55, 56, 57
        })
        {
            final long x = 1L << exponent;
            ALL_LONG_CANDIDATES.add(x);
            ALL_LONG_CANDIDATES.add(x + 1);
            ALL_LONG_CANDIDATES.add(x - 1);
        }

        ALL_LONG_CANDIDATES.add(9999L);
        ALL_LONG_CANDIDATES.add(10000L);
        ALL_LONG_CANDIDATES.add(10001L);
        ALL_LONG_CANDIDATES.add(1000000L); // near powers of 10
        ALL_LONG_CANDIDATES.add(5792L);
        ALL_LONG_CANDIDATES.add(5793L);
        ALL_LONG_CANDIDATES.add(194368031998L);
        ALL_LONG_CANDIDATES.add(194368031999L);

        final List<Long> negativeLongCandidates = new ArrayList<>(ALL_LONG_CANDIDATES.size());

        for (final long l : ALL_LONG_CANDIDATES)
        {
            negativeLongCandidates.add(-l);
        }

        ALL_LONG_CANDIDATES.addAll(negativeLongCandidates);
        ALL_LONG_CANDIDATES.add(Long.MIN_VALUE);
        ALL_LONG_CANDIDATES.add(0L);
    }

    /**
     * @param val1 long
     * @param val2 long
     * @param expected long
     * @param actual long
     */
    private static void assertOperationEquals(final long val1, final long val2, final long expected, final long actual)
    {
        if (expected != actual)
        {
            Assertions.fail("Expected for " + val1 + " s+ " + val2 + " = " + expected + ", but got " + actual);
        }
    }

    /**
     * @param big {@link BigInteger}
     *
     * @return long
     */
    private static long saturatedCast(final BigInteger big)
    {
        if (big.compareTo(MAX_LONG) > 0)
        {
            return Long.MAX_VALUE;
        }

        if (big.compareTo(MIN_LONG) < 0)
        {
            return Long.MIN_VALUE;
        }

        return big.longValue();
    }

    /**
     *
     */
    @Test
    void testSaturatedAdd()
    {
        for (final long a : ALL_LONG_CANDIDATES)
        {
            for (final long b : ALL_LONG_CANDIDATES)
            {
                assertOperationEquals(a, b, saturatedCast(BigInteger.valueOf(a).add(BigInteger.valueOf(b))), GoogleNanoThrottle.saturatedAdd(a, b));
            }
        }
    }
}