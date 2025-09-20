// Created: 03.04.2021
package de.freese.base.core.concurrent;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.ExecutorUtils;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestBoundedExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestBoundedExecutor.class);

    private static ExecutorService executorService;

    @AfterAll
    static void afterAll() {
        ExecutorUtils.shutdown(executorService);
    }

    @BeforeAll
    static void beforeAll() {
        executorService = Executors.newCachedThreadPool();
    }

    static Stream<Arguments> createTestData() {
        return Stream.of(
                Arguments.of("1 Thread", 1),
                Arguments.of("2 Threads", 2),
                Arguments.of("4 Threads", 4)
        );
    }

    private static void sleep() {
        // try {
        //     TimeUnit.MILLISECONDS.sleep(300);
        // }
        // catch (InterruptedException ex) {
        //     // Restore interrupted state.
        //     Thread.currentThread().interrupt();
        // }

        await().pollDelay(Duration.ofMillis(300)).until(() -> true);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutor")
    void testExecute(final String name, final int parallelism) {
        final BoundedExecutor boundedExecutor = new BoundedExecutor(executorService, parallelism);

        execute(boundedExecutor, null);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueued")
    void testExecuteQueued(final String name, final int parallelism) {
        final BoundedExecutorQueued boundedExecutor = new BoundedExecutorQueued(executorService, parallelism);

        execute(boundedExecutor, boundedExecutor::getQueueSize);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createTestData")
    @DisplayName("BoundedExecutorQueuedWithScheduler")
    void testExecuteQueuedWithScheduler(final String name, final int parallelism) {
        final BoundedExecutorQueuedWithScheduler boundedExecutor = new BoundedExecutorQueuedWithScheduler(executorService, parallelism);

        execute(boundedExecutor, boundedExecutor::getQueueSize);

        boundedExecutor.shutdown();
    }

    private void execute(final Executor executor, final Supplier<Integer> queueSizeSupplier) {
        final Runnable task = () -> {
            if (queueSizeSupplier == null) {
                LOGGER.info(Thread.currentThread().getName());
            }
            else {
                LOGGER.info("{}: QueueSize={}", Thread.currentThread().getName(), queueSizeSupplier.get());
            }

            sleep();
        };

        final List<FutureTask<Object>> futures = IntStream.range(0, 10).mapToObj(i -> {
            final FutureTask<Object> futureTask = new FutureTask<>(task, null);
            executor.execute(futureTask);
            return futureTask;
        }).toList();

        assertEquals(10, futures.size());

        futures.forEach(t -> {
            try {
                t.get();
            }
            catch (InterruptedException _) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException ex) {
                fail(ex);
            }
        });
    }
}
