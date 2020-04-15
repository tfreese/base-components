/**
 * Created: 01.02.2014
 */

package de.freese.base.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.pool.ObjectPoolBuilder.ObjectPoolType;
import de.freese.base.pool.factory.DefaultObjectFactory;
import de.freese.base.pool.roundRobin.RoundRobinPool;
import de.freese.base.pool.unbounded.UnboundedObjectPool;

/**
 * TestCase für die {@link ObjectPool} Implementierungen.<br>
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestObjectPoolBuilder
{
    /**
     *
     */
    private static final Supplier<UUID> CREATE_SUPPLIER = UUID::randomUUID;

    /**
    *
    */
    private static final List<Arguments> OBJECT_POOL_TYPES = new ArrayList<>();

    /**
    *
    */
    @BeforeAll
    static void beforeAll()
    {
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.COMMONS, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.ERASOFT, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.ROUND_ROBIN, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.SIMPLE, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.UNBOUNDED, CREATE_SUPPLIER));
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getObjectPoolTypes()
    {
        return OBJECT_POOL_TYPES.stream();
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param creator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0100GetAndReturn(final ObjectPoolType poolType, final Supplier<UUID> creator)
    {
        ObjectPool<UUID> pool = new ObjectPoolBuilder<UUID>().registerShutdownHook(true).maxSize(1).type(poolType)
                .objectFactory(new DefaultObjectFactory<>(creator)).build();

        UUID uuid1 = pool.borrowObject();
        assertNotNull(uuid1);
        pool.returnObject(uuid1);
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param creator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0200Equals(final ObjectPoolType poolType, final Supplier<UUID> creator)
    {
        ObjectPool<UUID> pool = new ObjectPoolBuilder<UUID>().registerShutdownHook(true).maxSize(1).type(poolType)
                .objectFactory(new DefaultObjectFactory<>(creator)).build();

        UUID uuid1 = pool.borrowObject();
        assertEquals(1, pool.getTotalSize());
        assertEquals(1, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());
        assertNotNull(uuid1);
        pool.returnObject(uuid1);

        UUID uuid2 = pool.borrowObject();
        assertEquals(1, pool.getTotalSize());
        assertEquals(1, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());
        assertNotNull(uuid2);
        assertEquals(uuid1, uuid2);
        pool.returnObject(uuid2);
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param creator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0300OverSize(final ObjectPoolType poolType, final Supplier<UUID> creator)
    {
        ObjectPool<UUID> pool = new ObjectPoolBuilder<UUID>().registerShutdownHook(true).maxSize(1).type(poolType)
                .objectFactory(new DefaultObjectFactory<>(creator)).build();

        if ((pool instanceof RoundRobinPool) || (pool instanceof UnboundedObjectPool))
        {
            // Test für diese ObjectsPools nicht anwendbar da keine Obergrenze vorhanden.
            return;
        }

        assertEquals(0, pool.getNumActive());

        UUID uuid1 = pool.borrowObject();

        assertEquals(1, pool.getTotalSize());
        assertEquals(1, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());

        try
        {
            // 2. Aufruf bei Pool mit Größe 1 -> Fehler
            pool.borrowObject();

            assertTrue(false);
        }
        catch (NoSuchElementException ex)
        {
            assertTrue(true);
            pool.returnObject(uuid1);
        }
    }
}
