// Created: 14.04.2020
package de.freese.base.utils;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestStopWatch {
    @Test
    void testNanoConvert() {
        final long nanos = 1_000_000_000L;

        assertEquals(1_000_000_000L, TimeUnit.NANOSECONDS.convert(nanos, TimeUnit.NANOSECONDS));
        assertEquals(1_000_000L, TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS));
        assertEquals(1_000L, TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS));

        assertEquals(1L, TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS));
    }

    @Test
    void testPrettyPrint() throws InterruptedException {
        final StopWatch stopWatch = new StopWatch();
        assertEquals("StopWatch-1", stopWatch.getId());

        stopWatch.start();
        assertEquals("Task-1", stopWatch.getCurrentTaskName());
        await().pollDelay(Duration.ofMillis(250)).until(() -> true);
        stopWatch.stop();

        stopWatch.start();
        assertEquals("Task-2", stopWatch.getCurrentTaskName());
        await().pollDelay(Duration.ofMillis(500)).until(() -> true);
        stopWatch.stop();

        stopWatch.start("A Task");
        assertEquals("A Task", stopWatch.getCurrentTaskName());
        await().pollDelay(Duration.ofMillis(750)).until(() -> true);
        stopWatch.stop();

        stopWatch.start("Task with a really really long long Name");
        assertEquals("Task with a really really long long Name", stopWatch.getCurrentTaskName());
        await().pollDelay(Duration.ofMillis(1000)).until(() -> true);
        stopWatch.stop();

        stopWatch.prettyPrint(System.out);
    }
}
