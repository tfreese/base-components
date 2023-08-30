// Created: 30.08.23
package de.freese.base.core.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestPools {

    @Test
    void testObjectPool() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<Integer> onCloseList = new ArrayList<>();

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
    void testRoundRobinPool() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<Integer> onCloseList = new ArrayList<>();

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
