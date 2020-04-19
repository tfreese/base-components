/**
 * Created: 14.04.2020
 */

package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
public class TestStopWatch
{
    /**
     *
     */
    @Test
    public void testNanoConvert()
    {
        long nanos = 1_000_000_000L;

        assertEquals(1_000_000_000L, TimeUnit.NANOSECONDS.convert(nanos, TimeUnit.NANOSECONDS));
        assertEquals(1_000_000L, TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS));
        assertEquals(1_000L, TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS));

        assertEquals(1L, TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS));
    }

    /**
     * @throws InterruptedException Falls was schief geht.
     */
    @Test
    public void testPrettyPrint() throws InterruptedException
    {
        StopWatch stopWatch = new StopWatch();
        assertEquals("StopWatch-1", stopWatch.getId());

        stopWatch.start();
        assertEquals("Task-1", stopWatch.getCurrentTaskName());
        TimeUnit.MILLISECONDS.sleep(250);
        stopWatch.stop();

        stopWatch.start();
        assertEquals("Task-2", stopWatch.getCurrentTaskName());
        TimeUnit.MILLISECONDS.sleep(500);
        stopWatch.stop();

        stopWatch.start("A Task");
        assertEquals("A Task", stopWatch.getCurrentTaskName());
        TimeUnit.MILLISECONDS.sleep(750);
        stopWatch.stop();

        stopWatch.start("Task with a really really long long Name");
        assertEquals("Task with a really really long long Name", stopWatch.getCurrentTaskName());
        TimeUnit.MILLISECONDS.sleep(1000);
        stopWatch.stop();

        stopWatch.prettyPrint();
    }
}
