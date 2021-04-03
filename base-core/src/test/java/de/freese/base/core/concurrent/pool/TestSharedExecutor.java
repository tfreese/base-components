// Created: 03.04.2021
package de.freese.base.core.concurrent.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.base.utils.ExecutorUtils;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSharedExecutor
{
    /**
    *
    */
    private static ExecutorService executorService;

    /**
    *
    */
    @AfterAll
    static void afterAll()
    {
        ExecutorUtils.shutdown(executorService);
    }

    /**
    *
    */
    @BeforeAll
    static void beforeAll()
    {
        executorService = Executors.newFixedThreadPool(8);
    }

    /**
     * @param seconds int
     */
    private static void sleep(final int seconds)
    {
        try
        {
            TimeUnit.SECONDS.sleep(seconds);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @param maxThreads int
     */
    private void maxThreadUsed(final int maxThreads)
    {
        System.out.println();

        SharedExecutor sharedExecutor = new SharedExecutor(executorService, maxThreads);

        Runnable task = () -> sleep(2);

        IntStream.range(0, 10).forEach(i -> sharedExecutor.execute(task));

        for (int i = 0; i < 10; i++)
        {
            assertTrue(sharedExecutor.getActiveTasks() <= maxThreads);

            if (sharedExecutor.getActiveTasks() == 0)
            {
                break;
            }

            sleep(2);
        }

        sharedExecutor.execute(task);
        assertEquals(1, sharedExecutor.getActiveTasks());
    }

    /**
    *
    */
    @Test
    void testInvalidParameter()
    {
        assertThrows(NullPointerException.class, () -> new SharedExecutor(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new SharedExecutor(executorService, 0));
        assertThrows(IllegalArgumentException.class, () -> new SharedExecutor(executorService, -1));
    }

    /**
    *
    */
    @Test
    void testMax1ThreadUsed()
    {
        maxThreadUsed(1);
    }

    /**
     *
     */
    @Test
    void testMax2ThreadUsed()
    {
        maxThreadUsed(2);
    }

    /**
    *
    */
    @Test
    void testMax4ThreadUsed()
    {
        maxThreadUsed(4);
    }
}
