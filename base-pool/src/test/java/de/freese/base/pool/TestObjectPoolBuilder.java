/**
 * Created: 01.02.2014
 */

package de.freese.base.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.pool.simple.RoundRobinPool;

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
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.SIMPLE, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.ROUND_ROBIN, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.COMMONS, CREATE_SUPPLIER));
        OBJECT_POOL_TYPES.add(Arguments.of(ObjectPoolType.ERASOFT, CREATE_SUPPLIER));
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getObjectPoolTypes()
    {
        return OBJECT_POOL_TYPES.stream();
    }

    /**
     * Erzeugt eine neue Instanz von {@link TestObjectPoolBuilder}
     */
    public TestObjectPoolBuilder()
    {
        super();
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0100GetAndReturn(final ObjectPoolType poolType, final Supplier<UUID> createSupplier)
    {
        ObjectPool<UUID> pool = ObjectPoolBuilder.create().max(1).maxWait(1234).registerShutdownHook(true).build(poolType, createSupplier);

        UUID uuid1 = pool.borrowObject();
        assertNotNull(uuid1);
        pool.returnObject(uuid1);
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0200Equals(final ObjectPoolType poolType, final Supplier<UUID> createSupplier)
    {
        ObjectPool<UUID> pool = ObjectPoolBuilder.create().max(1).registerShutdownHook(true).build(poolType, createSupplier);

        UUID uuid1 = pool.borrowObject();
        assertNotNull(uuid1);
        pool.returnObject(uuid1);

        UUID uuid2 = pool.borrowObject();
        assertNotNull(uuid2);
        assertEquals(uuid1, uuid2);
        pool.returnObject(uuid2);
    }

    /**
     * @param poolType {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getObjectPoolTypes")
    public void test0300OverSize(final ObjectPoolType poolType, final Supplier<UUID> createSupplier)
    {
        ObjectPool<UUID> pool = ObjectPoolBuilder.create().max(1).registerShutdownHook(true).build(poolType, createSupplier);

        UUID uuid1 = pool.borrowObject();

        try
        {
            pool.borrowObject();

            if (!(pool instanceof RoundRobinPool))
            {
                assertTrue(false);
            }
        }
        catch (Exception ex)
        {
            assertTrue(true);
            pool.returnObject(uuid1);
        }
    }
}
