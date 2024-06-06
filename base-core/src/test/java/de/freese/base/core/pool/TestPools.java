// Created: 30.08.23
package de.freese.base.core.pool;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestPools {

    @Test
    void testAbstractObjectPool() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        final AbstractObjectPool<Integer> pool = new AbstractObjectPool<>() {
            @Override
            protected Integer create() {
                return atomicInteger.incrementAndGet();
            }
        };

        Integer value1 = pool.get();
        assertEquals(1, value1);

        Integer value2 = pool.get();
        assertEquals(2, value2);
        pool.free(value2);

        value2 = pool.get();
        assertEquals(2, value2);
        pool.free(value2);

        pool.free(value1);

        value2 = pool.get();
        assertEquals(2, value2);
        pool.free(value2);

        value1 = pool.get();
        assertEquals(1, value1);
    }

    @Test
    void testObjectPool() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final List<Integer> onCloseList = new ArrayList<>();

        try (ObjectPool<Integer> pool = new ObjectPool<>(atomicInteger::incrementAndGet, onCloseList::add)) {
            Integer value1 = pool.get();
            assertEquals(1, value1);

            Integer value2 = pool.get();
            assertEquals(2, value2);
            pool.free(value2);

            value2 = pool.get();
            assertEquals(2, value2);
            pool.free(value2);

            pool.free(value1);

            value2 = pool.get();
            assertEquals(2, value2);
            pool.free(value2);

            value1 = pool.get();
            assertEquals(1, value1);

            assertEquals(2, pool.getTotalSize());
            assertEquals(1, pool.getNumActive());
            assertEquals(1, pool.getNumIdle());
        }

        assertEquals(2, onCloseList.size());
        assertEquals(2, onCloseList.get(0));
        assertEquals(1, onCloseList.get(1));
    }

    @Test
    void testObjectPoolExpiry() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final List<Integer> onCloseList = new ArrayList<>();

        try (ObjectPool<Integer> pool = new ObjectPool<>(atomicInteger::incrementAndGet, onCloseList::add)) {
            pool.setExpirationDuration(Duration.ofMillis(25));

            Integer value = pool.get();
            assertEquals(1, value);
            pool.free(value);

            value = pool.get();
            assertEquals(1, value);
            pool.free(value);

            await().pollDelay(Duration.ofMillis(50)).until(() -> true);

            value = pool.get();
            assertEquals(2, value);
            pool.free(value);

            value = pool.get();
            assertEquals(2, value);
            pool.free(value);

            assertEquals(1, pool.getTotalSize());
            assertEquals(0, pool.getNumActive());
            assertEquals(1, pool.getNumIdle());
        }

        assertEquals(2, onCloseList.size());
        assertEquals(1, onCloseList.get(0));
        assertEquals(2, onCloseList.get(1));
    }

    @Test
    void testRoundRobinPool() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final List<Integer> onCloseList = new ArrayList<>();

        try (RoundRobinPool<Integer> pool = new RoundRobinPool<>(3, atomicInteger::incrementAndGet, onCloseList::add)) {
            assertEquals(1, pool.get());
            assertEquals(2, pool.get());
            assertEquals(3, pool.get());

            assertEquals(1, pool.get());
        }

        assertEquals(3, onCloseList.size());
        assertEquals(1, onCloseList.get(0));
        assertEquals(2, onCloseList.get(1));
        assertEquals(3, onCloseList.get(2));
    }
}
