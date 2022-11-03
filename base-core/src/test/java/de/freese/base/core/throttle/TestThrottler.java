// Created: 29.03.2020
package de.freese.base.core.throttle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestThrottler //extends AbstractIoTest
{
    static Stream<Arguments> createThrottler()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("SimpleThrottler", (Function<Integer, Throttler>) SimpleThrottler::create)
                , Arguments.of("Failsafe", (Function<Integer, Throttler>) FailsafeThrottlerAdapter::create)
                , Arguments.of("Resilience4J", (Function<Integer, Throttler>) Resilience4JThrottlerAdapter::create)
                );
        // @formatter:on
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits2000(final String name, final Function<Integer, Throttler> throttleFunction) throws Exception
    {
        doTest(name, 2000, throttleFunction);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits4000(final String name, final Function<Integer, Throttler> throttleFunction) throws Exception
    {
        doTest(name, 4000, throttleFunction);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits6000(final String name, final Function<Integer, Throttler> throttleFunction) throws Exception
    {
        doTest(name, 6000, throttleFunction);
    }

    private void doTest(String name, final int permits, final Function<Integer, Throttler> throttleFunction) throws Exception
    {
        Throttler throttler = throttleFunction.apply(permits);

        // Warmup...sonst stimmen komischerweise bei Failsafe und Resilience4J die Raten nicht.
        throttler.acquirePermit();

        int size = 12_000;

        long start = System.nanoTime();

        for (int i = 0; i < size; i++)
        {
            throttler.acquirePermit();
        }

        long elapsed = System.nanoTime() - start;

        double rate = size / (elapsed / 1_000_000_000D);
        double timeMillis = (double) elapsed / 1_000_000D;
        double delta = Math.abs(rate - permits);

        System.out.printf("%-20s; Time = %9.3f ms; Permits = %d; Rate = %8.3f 1/s; Delta = %8.3f%n", name, timeMillis, permits, rate, delta);

        assertEquals(permits, rate, permits * 0.01D); // Max. 1 % Abweichung
    }
}
