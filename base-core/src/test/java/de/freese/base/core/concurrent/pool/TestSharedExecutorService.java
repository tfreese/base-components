// Created: 03.04.2021
package de.freese.base.core.concurrent.pool;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
class TestSharedExecutorService
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
     * @param maxThreads int
     */
    private void execute(final int maxThreads)
    {
        System.out.println();

        SharedExecutorService sharedExecutorService = new SharedExecutorService(executorService, maxThreads);

        Runnable task = () -> sleep();

        IntStream.range(0, 10).forEach(i -> sharedExecutorService.execute(task));

        for (int i = 0; i < 10; i++)
        {
            assertTrue(sharedExecutorService.getActiveTasks() <= maxThreads);

            if (sharedExecutorService.getActiveTasks() == 0)
            {
                break;
            }

            sleep();
        }

        sharedExecutorService.execute(task);
        assertTrue(sharedExecutorService.getActiveTasks() <= maxThreads);

        sharedExecutorService.submit(task);
        assertTrue(sharedExecutorService.getActiveTasks() <= maxThreads);

        sharedExecutorService.submit(task, null);
        assertTrue(sharedExecutorService.getActiveTasks() <= maxThreads);

        sharedExecutorService.submit((Callable<Void>) () -> {
            sleep();
            return null;
        });
        assertTrue(sharedExecutorService.getActiveTasks() <= maxThreads);
    }

    /**
     * @param maxThreads int
     * @throws Exception Falls was schief geht.
     */
    private void invokeAll(final int maxThreads) throws Exception
    {
        System.out.println();

        SharedExecutorService sharedExecutorService = new SharedExecutorService(executorService, maxThreads);

        Callable<Void> task = () -> {
            sleep();
            return null;
        };

        List<Callable<Void>> tasks = IntStream.range(0, 10).mapToObj(i -> task).collect(Collectors.toList());

        sharedExecutorService.invokeAll(tasks);

        for (int i = 0; i < 10; i++)
        {
            assertTrue(sharedExecutorService.getActiveTasks() <= 1);

            if (sharedExecutorService.getActiveTasks() == 0)
            {
                break;
            }

            sleep();
        }
    }

    /**
    *
    */
    @Test
    void testExecute1Thread()
    {
        execute(1);
    }

    /**
     *
     */
    @Test
    void testExecute2Thread()
    {
        execute(2);
    }

    /**
    *
    */
    @Test
    void testExecute4Thread()
    {
        execute(4);
    }

    /**
    *
    */
    @Test
    void testInvalidParameter()
    {
        assertThrows(NullPointerException.class, () -> new SharedExecutorService(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new SharedExecutorService(executorService, 0));
        assertThrows(IllegalArgumentException.class, () -> new SharedExecutorService(executorService, -1));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInvoceAll1Thread() throws Exception
    {
        invokeAll(1);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInvoceAll2Thread() throws Exception
    {
        invokeAll(2);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInvoceAll4Thread() throws Exception
    {
        invokeAll(4);
    }

    /**
    *
    */
    @Test
    void testUnsupportedMethods()
    {
        SharedExecutorService sharedExecutorService = new SharedExecutorService(executorService, 1);

        assertThrows(UnsupportedOperationException.class, () -> sharedExecutorService.awaitTermination(0, null));
        assertThrows(UnsupportedOperationException.class, () -> sharedExecutorService.invokeAny(null));
        assertThrows(UnsupportedOperationException.class, () -> sharedExecutorService.invokeAny(null, 0, null));
        assertThrows(UnsupportedOperationException.class, () -> sharedExecutorService.shutdown());
        assertThrows(UnsupportedOperationException.class, () -> sharedExecutorService.shutdownNow());
    }
}
