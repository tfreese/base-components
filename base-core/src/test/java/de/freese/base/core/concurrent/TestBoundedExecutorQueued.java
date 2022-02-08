// Created: 03.04.2021
package de.freese.base.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import de.freese.base.utils.ExecutorUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestBoundedExecutorQueued
{
    /**
    *
    */
    private static ExecutorService executorService;

    /**
     *
     */
    private static void sleep()
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(300);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

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
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * @param parallelism int
     */
    private void execute(final int parallelism)
    {
        System.out.println();

        BoundedExecutorQueued boundedExecutor = new BoundedExecutorQueued(executorService, parallelism);

        Callable<Void> task = () -> {
            System.out.printf("%s: QueueSize=%d%n", Thread.currentThread().getName(), boundedExecutor.getQueueSize());
            sleep();
            return null;
        };

        List<Future<Void>> list = IntStream.range(0, 10).mapToObj(i -> {
            System.out.printf("%s: QueueSize=%d%n", Thread.currentThread().getName(), boundedExecutor.getQueueSize());
            return boundedExecutor.execute(task);
        }).toList();

        list.forEach(t -> {
            try
            {
                t.get();
            }
            catch (InterruptedException ex)
            {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex)
            {
                fail(ex);
            }
        });

        boundedExecutor.shutdown();
    }

    /**
    *
    */
    @Test
    void test1Thread()
    {
        execute(1);

        assertTrue(true);
    }

    /**
     *
     */
    @Test
    void test2Threads()
    {
        execute(2);

        assertTrue(true);
    }

    /**
    *
    */
    @Test
    void test4Threads()
    {
        execute(4);

        assertTrue(true);
    }
}
