package de.freese.base.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

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
class TestCompletableFuture
{
    private static ExecutorService executorService;

    @AfterAll
    static void afterAll()
    {
        ExecutorUtils.shutdown(executorService);
    }

    @BeforeAll
    static void beforeAll()
    {
        executorService = Executors.newFixedThreadPool(4);
    }

    String getCurrentThreadName()
    {
        return Thread.currentThread().getName();
    }

    String getPoolName(final String threadName)
    {
        int firstIndex = threadName.indexOf('-');
        int secondIndex = threadName.indexOf('-', firstIndex + 1);

        return threadName.substring(0, secondIndex);
    }

    @Test
    void test010RunAsyncThenAccept() throws Exception
    {
        String currentThreadName = getCurrentThreadName();
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenAccept(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameCurrent = getPoolName(currentThreadName);
        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("ForkJoinPool"), nameThen + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen, poolNameCurrent + " != " + poolNameThen);
        assertNotEquals(poolNameRun, poolNameThen, poolNameRun + " != " + poolNameThen);
    }

    @Test
    void test011RunAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenAcceptAsync(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("pool"), nameThen + " not starts with 'pool'");

        assertEquals(poolNameRun, poolNameThen, poolNameRun + " != " + poolNameThen);
    }

    @Test
    void test012RunAsyncThenApply() throws Exception
    {
        String currentThreadName = getCurrentThreadName();
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenApply(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
            return null;
        });

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameCurrent = getPoolName(currentThreadName);
        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(currentThreadName.startsWith("ForkJoinPool"), currentThreadName + " not starts with 'ForkJoinPool'");
        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("ForkJoinPool"), nameThen + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen, poolNameCurrent + " != " + poolNameThen);
        assertNotEquals(poolNameRun, poolNameThen, poolNameRun + " == " + poolNameThen);
    }

    @Test
    void test013RunAsyncThenApplyAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenApplyAsync(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
            return null;
        }, executorService);

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("pool"), nameThen + " not starts with 'pool'");

        assertEquals(poolNameRun, poolNameThen, poolNameRun + " != " + poolNameThen);
    }

    @Test
    void test014RunAsyncThenRun() throws Exception
    {
        String currentThreadName = getCurrentThreadName();
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenRun(() ->
        {
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameCurrent = getPoolName(currentThreadName);
        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(currentThreadName.startsWith("ForkJoinPool"), currentThreadName + " not starts with 'ForkJoinPool'");
        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("ForkJoinPool"), nameThen + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen, poolNameCurrent + " != " + poolNameThen);
        assertNotEquals(poolNameRun, poolNameThen, poolNameRun + " == " + poolNameThen);
    }

    @Test
    void test015RunAsyncThenRunAsync() throws Exception
    {
        AtomicReference<String> threadNameRun = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() ->
        {
            threadNameRun.set(getCurrentThreadName());
        }, executorService).thenRunAsync(() ->
        {
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        String nameRun = threadNameRun.get();
        String nameThen = threadNameThen.get();

        String poolNameRun = getPoolName(nameRun);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(nameRun.startsWith("pool"), nameRun + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("pool"), nameThen + " not starts with 'pool'");

        assertEquals(poolNameRun, poolNameThen, poolNameRun + " != " + poolNameThen);
    }

    @Test
    void test020SupplyAsyncThenAccept() throws Exception
    {
        String currentThreadName = getCurrentThreadName();
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
        {
            threadNameSupply.set(getCurrentThreadName());
            return null;
        }, executorService).thenAccept(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
        });

        cf.get();

        String nameSupply = threadNameSupply.get();
        String nameThen = threadNameThen.get();

        String poolNameCurrent = getPoolName(currentThreadName);
        String poolNameSupply = getPoolName(nameSupply);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(currentThreadName.startsWith("ForkJoinPool"), currentThreadName + " not starts with 'ForkJoinPool'");
        assertTrue(nameSupply.startsWith("pool"), nameSupply + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("ForkJoinPool"), nameThen + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen, poolNameCurrent + " != " + poolNameThen);
        assertNotEquals(poolNameSupply, poolNameThen, poolNameSupply + " == " + poolNameThen);
    }

    @Test
    void test021SupplyAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
        {
            threadNameSupply.set(getCurrentThreadName());
            return null;
        }, executorService).thenAcceptAsync(VOID ->
        {
            threadNameThen.set(getCurrentThreadName());
        }, executorService);

        cf.get();

        String nameSupply = threadNameSupply.get();
        String nameThen = threadNameThen.get();

        String poolNameSupply = getPoolName(nameSupply);
        String poolNameThen = getPoolName(nameThen);

        assertTrue(nameSupply.startsWith("pool"), nameSupply + " not starts with 'pool'");
        assertTrue(nameThen.startsWith("pool"), nameThen + " not starts with 'pool'");

        assertEquals(poolNameSupply, poolNameThen, poolNameSupply + " != " + nameThen);
    }

    @Test
    void test022SupplyAsyncThenApplyThenAccept() throws Exception
    {
        String currentThreadName = getCurrentThreadName();
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen1 = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
        {
            threadNameSupply.set(getCurrentThreadName());
            return null;
        }, executorService).thenApply(VOID ->
        {
            threadNameThen1.set(getCurrentThreadName());
            return null;
        }).thenAccept(VOID ->
        {
            threadNameThen2.set(getCurrentThreadName());
        });

        cf.get();

        String nameSupply = threadNameSupply.get();
        String nameThen1 = threadNameThen1.get();
        String nameThen2 = threadNameThen2.get();

        String poolNameCurrent = getPoolName(currentThreadName);
        String poolNameSupply = getPoolName(nameSupply);
        String poolNameThen1 = getPoolName(nameThen1);
        String poolNameThen2 = getPoolName(nameThen2);

        assertTrue(currentThreadName.startsWith("ForkJoinPool"), currentThreadName + " not starts with 'ForkJoinPool'");
        assertTrue(nameSupply.startsWith("pool"), nameSupply + " not starts with 'pool'");
        assertTrue(nameThen1.startsWith("ForkJoinPool"), nameThen1 + " not starts with 'ForkJoinPool'");
        assertTrue(nameThen2.startsWith("ForkJoinPool"), nameThen2 + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen1, poolNameCurrent + " != " + poolNameThen1);
        assertEquals(poolNameCurrent, poolNameThen2, poolNameCurrent + " != " + poolNameThen2);
        assertNotEquals(poolNameSupply, poolNameThen1, poolNameSupply + " == " + poolNameThen1);
    }

    @Test
    void test023SupplyAsyncThenApplyAsyncThenAccept() throws Exception
    {
        String threadNameCurrent = getCurrentThreadName();
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen1 = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
        {
            threadNameSupply.set(getCurrentThreadName());
            return null;
        }, executorService).thenApplyAsync(VOID ->
        {
            threadNameThen1.set(getCurrentThreadName());
            return null;
        }, executorService).thenAccept(VOID ->
        {
            threadNameThen2.set(getCurrentThreadName());
        });

        cf.get();

        String nameSupply = threadNameSupply.get();
        String nameThen1 = threadNameThen1.get();
        String nameThen2 = threadNameThen2.get();

        String poolNameCurrent = getPoolName(threadNameCurrent);
        String poolNameSupply = getPoolName(nameSupply);
        String poolNameThen1 = getPoolName(nameThen1);
        String poolNameThen2 = getPoolName(nameThen2);

        assertTrue(threadNameCurrent.startsWith("ForkJoinPool"), threadNameCurrent + " not starts with 'ForkJoinPool'");
        assertTrue(nameSupply.startsWith("pool"), nameSupply + " not starts with 'pool'");
        assertTrue(nameThen1.startsWith("pool"), nameThen1 + " not starts with 'pool'");
        assertTrue(nameThen2.startsWith("ForkJoinPool"), nameThen2 + " not starts with 'ForkJoinPool'");

        assertEquals(poolNameCurrent, poolNameThen2, poolNameCurrent + " != " + poolNameThen2);
        assertEquals(poolNameSupply, poolNameThen1);
    }

    @Test
    void test024SupplyAsyncThenApplyAsyncThenAcceptAsync() throws Exception
    {
        AtomicReference<String> threadNameSupply = new AtomicReference<>("");
        AtomicReference<String> threadNameThen1 = new AtomicReference<>("");
        AtomicReference<String> threadNameThen2 = new AtomicReference<>("");

        CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() ->
        {
            threadNameSupply.set(getCurrentThreadName());
            return null;
        }, executorService).thenApplyAsync(VOID ->
        {
            threadNameThen1.set(getCurrentThreadName());
            return null;
        }, executorService).thenAcceptAsync(VOID -> threadNameThen2.set(getCurrentThreadName()), executorService);

        cf.get();

        String nameSupply = threadNameSupply.get();
        String nameThen1 = threadNameThen1.get();
        String nameThen2 = threadNameThen2.get();

        String poolNameSupply = getPoolName(nameSupply);
        String poolNameThen1 = getPoolName(nameThen1);
        String poolNameThen2 = getPoolName(nameThen2);

        assertTrue(nameSupply.startsWith("pool"), nameSupply + " not starts with 'pool'");
        assertTrue(nameThen1.startsWith("pool"), nameThen1 + " not starts with 'pool'");
        assertTrue(nameThen2.startsWith("pool"), nameThen2 + " not starts with 'pool'");

        assertEquals(poolNameSupply, poolNameThen1);
        assertEquals(poolNameSupply, poolNameThen2);
        assertEquals(poolNameThen1, poolNameThen2);
    }
}
