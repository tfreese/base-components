package de.freese.base.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.artsok.RepeatedIfExceptionsTest;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // Reproduziert auf Console nicht immer eindeutige Ergebnisse (flaky tests).
class TestCompletableFuture
{
    /**
     *
     */
    private static ExecutorService executorService;

    /**
     * Reproduziert auf Console nicht immer eindeutige Ergebnisse (flaky tests).
     */
    private static final int TEST_REPEATS = 5;

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        executorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS))
            {
                System.out.println("Timed out while waiting for executorService");

                // Cancel currently executing tasks.
                for (Runnable remainingTask : executorService.shutdownNow())
                {
                    if (remainingTask instanceof Future)
                    {
                        ((Future<?>) remainingTask).cancel(true);
                    }
                }

                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS))
                {
                    System.out.println("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            System.out.println("Interrupted while waiting for executorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        executorService = Executors.newFixedThreadPool(4);
        // executorService = Executors.newFixedThreadPool(4, new CustomizableThreadFactory.Builder().build());
    }

    /**
     * @return String
     */
    String getCurrentThreadName()
    {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName);
        // sleep();

        return threadName;
    }

    /**
     *
     */
    void sleep()
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test010RunAsyncThenAccept() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenAccept(VOID -> {
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertEquals("main", threadNameThen.get());
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test011RunAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenAcceptAsync(VOID -> {
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test012RunAsyncThenApply() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenApply(VOID -> {
            threadNameThen.set(getCurrentThreadName());
            return null;
        });

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertEquals("main", threadNameThen.get());
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test013RunAsyncThenApplyAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenApplyAsync(VOID -> {
            threadNameThen.set(getCurrentThreadName());
            return null;
        }, executorService);

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test014RunAsyncThenRun() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenRun(() -> {
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertEquals("main", threadNameThen.get());
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test015RunAsyncThenRunAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenRunAsync(() -> {
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        assertTrue(threadNameRun.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertNotEquals(threadNameRun.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test020SupplyAsyncThenAccept() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
            return getCurrentThreadName();
        }, executorService).thenAccept(name -> {
            threadNameSupply.set(name);
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        assertTrue(threadNameSupply.get().startsWith("pool"));
        assertEquals("main", threadNameThen.get());
        assertNotEquals(threadNameSupply.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test021SupplyAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
            return getCurrentThreadName();
        }, executorService).thenAcceptAsync(name -> {
            threadNameSupply.set(name);
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        assertTrue(threadNameSupply.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertNotEquals(threadNameSupply.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test022SupplyAsyncThenApplyThenAccept() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
            return getCurrentThreadName();
        }, executorService).thenApply(name -> {
            threadNameSupply.set(name);
            return getCurrentThreadName();
        }).thenAccept(name -> {
            threadNameThen.set(name);
            threadNameThen2.set(getCurrentThreadName());
        });

        cf.get();

        assertTrue(threadNameSupply.get().startsWith("pool"));
        assertEquals("main", threadNameThen.get());
        assertEquals("main", threadNameThen2.get());
        assertNotEquals(threadNameSupply.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test023SupplyAsyncThenApplyAsyncThenAccept() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
            return getCurrentThreadName();
        }, executorService).thenApplyAsync(name -> {
            threadNameSupply.set(name);
            return getCurrentThreadName();
        }, executorService).thenAccept(name -> {
            threadNameThen.set(name);
            threadNameThen2.set(getCurrentThreadName());
        });

        cf.get();

        assertTrue(threadNameSupply.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertEquals("main", threadNameThen2.get());
        assertNotEquals(threadNameSupply.get(), threadNameThen.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    // @RepeatedTest(TEST_REPEATS)
    @RepeatedIfExceptionsTest(repeats = TEST_REPEATS)
    void test024SupplyAsyncThenApplyAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
            return getCurrentThreadName();
        }, executorService).thenApplyAsync(name -> {
            threadNameSupply.set(name);
            return getCurrentThreadName();
        }, executorService).thenAcceptAsync(name -> {
            threadNameThen.set(name);
            threadNameThen2.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        assertTrue(threadNameSupply.get().startsWith("pool"));
        assertTrue(threadNameThen.get().startsWith("pool"));
        assertTrue(threadNameThen2.get().startsWith("pool"));
        assertNotEquals(threadNameSupply.get(), threadNameThen.get());
        assertNotEquals(threadNameSupply.get(), threadNameThen2.get());
        assertNotEquals(threadNameThen.get(), threadNameThen2.get());
    }
}