// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;

import de.freese.base.core.throttle.io.FakeInputStream;
import de.freese.base.core.throttle.io.ThrottledInputStream;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
//@TestMethodOrder(MethodOrderer.MethodName.class)
@Execution(ExecutionMode.CONCURRENT)
class ThrottleInputStreamTest //extends AbstractIoTest
{
    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("UnderstandableThrottle", (Function<Integer, Throttle>) (UnderstandableThrottle::create))
                );
        // @formatter:on
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     *
     * @throws IOException Falls was schiefgeht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits2000(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 2000);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     *
     * @throws IOException Falls was schiefgeht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits4000(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 4000);
    }

    /**
     * @param name String
     * @param throttleFunction {@link Function}
     *
     * @throws IOException Falls was schiefgeht.
     */
    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits6000(final String name, final Function<Integer, Throttle> throttleFunction) throws IOException
    {
        doTest(throttleFunction, 6000);
    }

    /**
     * @param throttleFunction {@link Function}
     * @param permits int
     *
     * @throws IOException Falls was schiefgeht.
     */
    private void doTest(final Function<Integer, Throttle> throttleFunction, final int permits) throws IOException
    {
        int size = 12 * 1024;

        try (InputStream inputStream = new FakeInputStream(size);
             ThrottledInputStream throttledStream = new ThrottledInputStream(inputStream, throttleFunction.apply(permits)))
        {
            long start = System.nanoTime();

            while (throttledStream.read() != -1)
            {
                // Empty
            }

            long elapsed = System.nanoTime() - start;
            double rate = size / (elapsed / 1_000_000_000D);

            System.out.printf("Time = %.3f ms; Permits = %d; Rate = %.3f b/s%n", (double) elapsed / 1_000_000D, permits, rate);

            assertEquals(permits, rate, permits * 0.02D); // Max. 2% Abweichung
        }
    }
}
