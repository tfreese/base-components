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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestThrottler // extends AbstractIoTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestThrottler.class);

    static Stream<Arguments> createThrottler() {
        return Stream.of(
                Arguments.of("SimpleThrottler", (Function<Integer, Throttler>) SimpleThrottler::create),
                Arguments.of("Failsafe", (Function<Integer, Throttler>) FailsafeThrottlerAdapter::create),
                Arguments.of("Resilience4J", (Function<Integer, Throttler>) Resilience4JThrottlerAdapter::create)
        );
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits2000(final String name, final Function<Integer, Throttler> throttleFunction) {
        doTest(name, 2000, throttleFunction);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits4000(final String name, final Function<Integer, Throttler> throttleFunction) {
        doTest(name, 4000, throttleFunction);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createThrottler")
    void testPermits6000(final String name, final Function<Integer, Throttler> throttleFunction) {
        doTest(name, 6000, throttleFunction);
    }

    private void doTest(final String name, final int permits, final Function<Integer, Throttler> throttleFunction) {
        final Throttler throttler = throttleFunction.apply(permits);

        // Warmup: sonst stimmen komischerweise bei Failsafe und Resilience4J die Raten nicht.
        throttler.acquirePermit();

        final int size = 12_000;

        final long start = System.nanoTime();

        for (int i = 0; i < size; i++) {
            throttler.acquirePermit();
        }

        final long elapsed = System.nanoTime() - start;

        final double rate = size / (elapsed / 1_000_000_000D);
        final double timeMillis = (double) elapsed / 1_000_000D;
        final double delta = Math.abs(rate - permits);

        final String message = "%-20s; Time = %9.3f ms; Permits = %d; Rate = %8.3f 1/s; Delta = %8.3f%n".formatted(name, timeMillis, permits, rate, delta);
        LOGGER.info(message);

        assertEquals(permits, rate, permits * 0.01D); // Max. 1 % Abweichung
    }
}
