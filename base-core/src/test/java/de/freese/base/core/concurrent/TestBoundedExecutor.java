// Created: 03.04.2021
package de.freese.base.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.freese.base.utils.ExecutorUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
     * @return {@link Stream}
     */
    static Stream<Arguments> createTestData()
    {
        // @formatter:off
        return Stream.of(
                Arguments.of("1 Thread", 1),
                Arguments.of("2 Threads", 2),
                Arguments.of("4 Threads", 4)
                );
        // @formatter:on
    }

    /**
     * @param executor {@link Function}
     * @param queueSizeSupplier {@link Supplier}
     */
    private void execute(final Function<Callable<Void>, Future<Void>> executor, final Supplier<Integer> queueSizeSupplier)
    {
        System.out.println();

        Callable<Void> task = () -> {
            if (queueSizeSupplier == null)
            {
                System.out.printf("%s%n", Thread.currentThread().getName());
            }
            else
            {
                System.out.printf("%s: QueueSize=%d%n", Thread.currentThread().getName(), queueSizeSupplier.get());
            }

            sleep();
            return null;
        };

        List<Future<Void>> futures = IntStream.range(0, 10).mapToObj(i -> executor.apply(task)).toList();

        assertEquals(10, futures.size());

        futures.forEach(t -> {
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
    }

    /**
     * @param name String
     * @param parallelism int
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutor")
    void execute(final String name, final int parallelism)
    {
        BoundedExecutor boundedExecutor = new BoundedExecutor(executorService, parallelism);

        execute(boundedExecutor::execute, null);
    }

    /**
     * @param name String
     * @param parallelism int
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueued")
    void executeQueued(final String name, final int parallelism)
    {
        BoundedExecutorQueued boundedExecutor = new BoundedExecutorQueued(executorService, parallelism);

        execute(boundedExecutor::execute, boundedExecutor::getQueueSize);
    }

    /**
     * @param name String
     * @param parallelism int
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueuedWithScheduler")
    void executeQueuedWithScheduler(final String name, final int parallelism)
    {
        BoundedExecutorQueuedWithScheduler boundedExecutor = new BoundedExecutorQueuedWithScheduler(executorService, parallelism);

        execute(boundedExecutor::execute, boundedExecutor::getQueueSize);
    }
}
