// Created: 30.08.23
package de.freese.base.core.pool;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.freese.base.core.pool.apache.ApachePoolFactory;
import de.freese.base.core.pool.simple.SimplePoolFactory;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestPools {

    @AfterAll
    static void afterAll() {
        PoolManager.close();
    }

    @BeforeAll
    static void beforeAll() {
        assertNotNull(PoolManager.createPool("simple", new SimplePoolFactory<>().expiry(Duration.ofMillis(25L)).objectSupplier(LocalDateTime::now)));
        assertNotNull(PoolManager.createPool("apache", new ApachePoolFactory<>().expiry(Duration.ofMillis(25L)).objectSupplier(LocalDateTime::now)));
    }

    static Stream<Arguments> getCaches() {
        return Stream.of(
                Arguments.of("simple"),
                Arguments.of("apache")
        );
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getCaches")
    void testPool(final String name) throws Exception {
        final Pool<LocalDateTime> pool = PoolManager.getPool(name);
        assertNotNull(pool);

        final LocalDateTime localDateTime = pool.getObject();
        assertNotNull(localDateTime);
        pool.returnObject(localDateTime);

        try (PooledObject<LocalDateTime> pooledObject = pool.getPooledObject()) {
            assertNotNull(pooledObject);
            assertNotNull(pooledObject.getObject());
            assertEquals(localDateTime, pooledObject.getObject());
        }
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getCaches")
    void testPoolExpiry(final String name) throws Exception {
        final Pool<LocalDateTime> pool = PoolManager.getPool(name);
        assertNotNull(pool);

        if ("simple".equals(name)) {
            return;
        }

        final LocalDateTime localDateTime = pool.getObject();
        assertNotNull(localDateTime);
        pool.returnObject(localDateTime);

        try (PooledObject<LocalDateTime> pooledObject = pool.getPooledObject()) {
            assertNotNull(pooledObject);
            assertNotNull(pooledObject.getObject());
            assertEquals(localDateTime, pooledObject.getObject());
        }

        await().pollDelay(Duration.ofMillis(50)).until(() -> true);

        final LocalDateTime localDateTime1 = pool.getObject();
        assertNotNull(localDateTime1);
        assertNotEquals(localDateTime, localDateTime1);
        pool.returnObject(localDateTime1);

        try (PooledObject<LocalDateTime> pooledObject = pool.getPooledObject()) {
            assertNotNull(pooledObject);
            assertNotNull(pooledObject.getObject());
            assertEquals(localDateTime1, pooledObject.getObject());
            assertNotEquals(localDateTime, pooledObject.getObject());
        }
    }

    @Test
    void testRoundRobinPool() throws Exception {
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
