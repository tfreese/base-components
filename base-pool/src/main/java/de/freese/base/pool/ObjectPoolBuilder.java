/**
 * Created: 17.10.2017
 */
package de.freese.base.pool;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import de.freese.base.pool.commons.CommonsObjectFactoryAdapter;
import de.freese.base.pool.commons.CommonsObjectPoolAdapter;
import de.freese.base.pool.erasoft.ErasoftObjectFactoryAdapter;
import de.freese.base.pool.erasoft.ErasoftObjectPoolAdapter;
import de.freese.base.pool.factory.FunctionalObjectFactory;
import de.freese.base.pool.factory.ObjectFactory;
import de.freese.base.pool.roundRobin.RoundRobinPool;
import de.freese.base.pool.simple.SimpleObjectPool;
import de.freese.base.pool.unbounded.UnboundedObjectPool;
import nf.fr.eraasoft.pool.PoolSettings;

/**
 * Erzeugt einen {@link ObjectPool} aus verschiedenen Implementierungen.<br>
 * Default Konfiguration ist ohne Objekt-Limits und ohne Validierungen.
 *
 * @author Thomas Freese
 */
public class ObjectPoolBuilder extends AbstractPoolBuilder<ObjectPoolBuilder>
{
    /**
     * Erzeugt einen neuen Builder.
     *
     * @return {@link ObjectPoolBuilder}
     */
    public static ObjectPoolBuilder create()
    {
        return new ObjectPoolBuilder();
    }

    /**
     * Erzeugt eine neue Instanz von {@link ObjectPoolBuilder}
     */
    protected ObjectPoolBuilder()
    {
        super();
    }

    /**
     * Liefert einen {@link ObjectPool}.<br>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param type {@link ObjectPoolType}
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> build(final ObjectPoolType type, final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(type, "type required");
        Objects.requireNonNull(objectFactory, "objectFactory required");

        ObjectPool<O> objectPool = null;

        switch (type)
        {
            case COMMONS:
                objectPool = buildCommonsPool(objectFactory);
                break;

            case ERASOFT:
                objectPool = buildErasoftPool(objectFactory);
                break;

            case ROUND_ROBIN:
                objectPool = buildRoundRobinPool(objectFactory);
                break;

            case SIMPLE:
                objectPool = buildSimplePool(objectFactory);
                break;

            case UNBOUNDED:
                objectPool = buildUnboundedPool(objectFactory);
                break;

            default:
                throw new IllegalStateException("unexpected type: " + type);
        }

        return objectPool;
    }

    /**
     * Liefert einen {@link ObjectPool}.<br>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param type {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}; muss gesetzt sein
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> build(final ObjectPoolType type, final Supplier<O> createSupplier)
    {
        return build(type, createSupplier, null, null, null, null);
    }

    /**
     * Liefert einen {@link ObjectPool}.<br>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param type {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}; muss gesetzt sein
     * @param activateConsumer {@link Consumer}; optional
     * @param passivateConsumer {@link Consumer}; optional
     * @param destroyConsumer {@link Consumer}; optional
     * @param validateFunction {@link Function}; optional
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> build(final ObjectPoolType type, final Supplier<O> createSupplier, final Consumer<O> activateConsumer,
                                   final Consumer<O> passivateConsumer, final Consumer<O> destroyConsumer, final Function<O, Boolean> validateFunction)
    {
        Objects.requireNonNull(type, "type required");
        Objects.requireNonNull(createSupplier, "createSupplier required");

        FunctionalObjectFactory<O> objectFactory = new FunctionalObjectFactory<>(createSupplier);
        objectFactory.setActivator(activateConsumer);
        objectFactory.setPassivator(passivateConsumer);
        objectFactory.setDestroyer(destroyConsumer);
        objectFactory.setValidator(validateFunction);

        return build(type, objectFactory);
    }

    /**
     * Liefert einen {@link ObjectPool}.<br>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param type {@link ObjectPoolType}
     * @param createSupplier {@link Supplier}; muss gesetzt sein
     * @param validateFunction {@link Function}; optional
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> build(final ObjectPoolType type, final Supplier<O> createSupplier, final Function<O, Boolean> validateFunction)
    {
        return build(type, createSupplier, null, null, null, validateFunction);
    }

    /**
     * Liefert einen {@link GenericObjectPool}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #coreSize}
     * <li>{@link #maxSize}
     * <li>{@link #validateOnGet}
     * <li>{@link #validateOnReturn}
     * </ul>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> buildCommonsPool(final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(objectFactory, "objectFactory required");
        CommonsObjectFactoryAdapter<O> objectFactoryAdapter = new CommonsObjectFactoryAdapter<>(objectFactory);

        GenericObjectPoolConfig<O> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(getMaxSize() <= 0 ? DEFAULT_MAX_SIZE : getMaxSize());
        config.setMaxIdle(config.getMaxTotal());
        config.setMaxWaitMillis(getMaxWait() <= 0 ? -1 : getMaxWait());
        config.setMinIdle(getCoreSize() <= 0 ? DEFAULT_CORE_SIZE : getCoreSize());
        config.setTestOnBorrow(isValidateOnGet());
        config.setTestOnReturn(isValidateOnReturn());
        config.setTestWhileIdle(false);
        config.setLifo(true);
        config.setMinEvictableIdleTimeMillis(-1);
        config.setNumTestsPerEvictionRun(0);
        config.setSoftMinEvictableIdleTimeMillis(-1);
        config.setTimeBetweenEvictionRunsMillis(-1);
        config.setBlockWhenExhausted(false);

        ObjectPool<O> objectPool = new CommonsObjectPoolAdapter<>(config, objectFactoryAdapter);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
    }

    /**
     * Liefert einen {@link ObjectPool}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #coreSize}
     * <li>{@link #maxSize}
     * <li>{@link #maxWait}
     * <li>{@link #validateOnReturn}
     * </ul>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> buildErasoftPool(final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(objectFactory, "objectFactory required");
        ErasoftObjectFactoryAdapter<O> objectFactoryAdapter = new ErasoftObjectFactoryAdapter<>(objectFactory);

        PoolSettings<O> settings = new PoolSettings<>(objectFactoryAdapter);
        settings.max(getMaxSize() <= 0 ? DEFAULT_MAX_SIZE : getMaxSize());
        settings.maxIdle(settings.max());
        settings.maxWait(getMaxWait() <= 0 ? 0 : getMaxWait() / 1000);
        settings.min(getCoreSize() <= 0 ? DEFAULT_CORE_SIZE : getCoreSize());
        settings.validateWhenReturn(isValidateOnReturn());

        ObjectPool<O> objectPool = new ErasoftObjectPoolAdapter<>(settings);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
    }

    /**
     * Liefert einen {@link RoundRobinPool}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #maxSize}
     * </ul>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> buildRoundRobinPool(final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(objectFactory, "objectFactory required");

        int maxSize = getMaxSize() <= 0 ? RoundRobinPool.DEFAULT_SIZE : getMaxSize();

        RoundRobinPool<O> objectPool = new RoundRobinPool<>(maxSize, objectFactory);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
    }

    /**
     * Liefert einen {@link SimpleObjectPool}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #coreSize}
     * <li>{@link #maxSize}
     * </ul>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> buildSimplePool(final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(objectFactory, "objectFactory required");

        int coreSize = getCoreSize() <= 0 ? DEFAULT_CORE_SIZE : getCoreSize();
        int maxSize = getMaxSize() <= 0 ? DEFAULT_MAX_SIZE : getMaxSize();

        SimpleObjectPool<O> objectPool = new SimpleObjectPool<>(coreSize, maxSize, objectFactory);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
    }

    /**
     * Liefert einen {@link UnboundedObjectPool}.<br>
     *
     * @param <O> Konkreter Typ der zu erzeugenden Objekte
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    public <O> ObjectPool<O> buildUnboundedPool(final ObjectFactory<O> objectFactory)
    {
        Objects.requireNonNull(objectFactory, "objectFactory required");

        UnboundedObjectPool<O> objectPool = new UnboundedObjectPool<>(objectFactory);

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
    }
}
