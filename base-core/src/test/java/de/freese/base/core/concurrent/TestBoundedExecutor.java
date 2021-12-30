// Created: 03.04.2021
package de.freese.base.core.concurrent;

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
class TestBoundedExecutor
{
    /**
    *
    */
    private static ExecutorService executorService;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
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
     *
     */
    private static void sleep()
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(500);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @param parallelism int
     */
    private void execute(final int parallelism)
    {
        System.out.println();

        BoundedExecutor boundedExecutor = new BoundedExecutor(executorService, parallelism);

        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName());
            sleep();
        };

        IntStream.range(0, 10).forEach(i -> boundedExecutor.execute(task));

        try
        {
            // Warten bis fertich.
            boundedExecutor.waitForPermits(parallelism);
        }
        catch (InterruptedException ex)
        {
            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }

    /**
    *
    */
    @Test
    void testExecute1Thread()
    {
        execute(1);

        assertTrue(true);
    }

    /**
     *
     */
    @Test
    void testExecute2Thread()
    {
        execute(2);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void testExecute4Thread()
    {
        execute(4);

        assertTrue(true);
    }
}
