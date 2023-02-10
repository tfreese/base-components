// Created: 03.04.2021
package de.freese.base.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.freese.base.utils.ExecutorUtils;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestBoundedExecutor {
    private static ExecutorService executorService;

    @AfterAll
    static void afterAll() throws Exception {
        ExecutorUtils.shutdown(executorService);
    }

    @BeforeAll
    static void beforeAll() {
        executorService = Executors.newCachedThreadPool();
    }

    static Stream<Arguments> createTestData() {
        // @formatter:off
        return Stream.of(
                Arguments.of("1 Thread", 1),
                Arguments.of("2 Threads", 2),
                Arguments.of("4 Threads", 4)
                );
        // @formatter:on
    }

    private static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutor")
    void testExecute(final String name, final int parallelism) {
        BoundedExecutor boundedExecutor = new BoundedExecutor(executorService, parallelism);

        execute(boundedExecutor, null);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueued")
    void testExecuteQueued(final String name, final int parallelism) {
        BoundedExecutorQueued boundedExecutor = new BoundedExecutorQueued(executorService, parallelism);

        execute(boundedExecutor, boundedExecutor::getQueueSize);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueuedWithScheduler")
    void testExecuteQueuedWithScheduler(final String name, final int parallelism) {
        BoundedExecutorQueuedWithScheduler boundedExecutor = new BoundedExecutorQueuedWithScheduler(executorService, parallelism);

        execute(boundedExecutor, boundedExecutor::getQueueSize);

        boundedExecutor.shutdown();
    }

    private void execute(final Executor executor, final Supplier<Integer> queueSizeSupplier) {
        System.out.println();

        Runnable task = () -> {
            if (queueSizeSupplier == null) {
                System.out.printf("%s%n", Thread.currentThread().getName());
            }
            else {
                System.out.printf("%s: QueueSize=%d%n", Thread.currentThread().getName(), queueSizeSupplier.get());
            }

            sleep();
        };

        // @formatter::off
        List<FutureTask<Object>> futures = IntStream.range(0, 10).mapToObj(i -> new FutureTask<>(task, null)).map(futureTask -> {
            executor.execute(futureTask);
            return futureTask;
        }).toList();
        // @formatter::on

        assertEquals(10, futures.size());

        futures.forEach(t -> {
            try {
                t.get();
            }
            catch (InterruptedException ex) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex) {
                fail(ex);
            }
        });
    }
}
