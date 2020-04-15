/**
 * Created: 17.10.2017
 */
package de.freese.base.pool;

import java.util.Objects;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import de.freese.base.pool.commons.CommonsObjectFactoryAdapter;
import de.freese.base.pool.commons.CommonsObjectPoolAdapter;
import de.freese.base.pool.erasoft.ErasoftObjectFactoryAdapter;
import de.freese.base.pool.erasoft.ErasoftObjectPoolAdapter;
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
 * @param <T> Typ des Objektes
 */
public class ObjectPoolBuilder<T> extends AbstractPoolBuilder<ObjectPoolBuilder<T>, ObjectPool<T>>
{
    /**
     * Enum für die Implementierungen des {@link ObjectPool}.
     *
     * @author Thomas Freese
     */
    public enum ObjectPoolType
    {
        /**
         *
         */
        COMMONS,

        /**
         *
         */
        ERASOFT,

        /**
        *
        */
        ROUND_ROBIN,

        /**
         *
         */
        SIMPLE,

        /**
        *
        */
        UNBOUNDED;
    }

    /**
     *
     */
    private ObjectFactory<T> objectFactory = null;

    /**
     *
     */
    private ObjectPoolType type = null;

    /**
     * Erzeugt eine neue Instanz von {@link ObjectPoolBuilder}
     */
    public ObjectPoolBuilder()
    {
        super();
    }

    /**
     * @see de.freese.base.core.model.builder.Builder#build()
     */
    @Override
    public ObjectPool<T> build()
    {
        Objects.requireNonNull(this.type, "ObjectPoolType required");
        Objects.requireNonNull(this.objectFactory, "objectFactory required");

        ObjectPool<T> objectPool = null;

        switch (this.type)
        {
            case COMMONS:
                objectPool = buildCommonsPool(this.objectFactory);
                break;

            case ERASOFT:
                objectPool = buildErasoftPool(this.objectFactory);
                break;

            case ROUND_ROBIN:
                objectPool = buildRoundRobinPool(this.objectFactory);
                break;

            case SIMPLE:
                objectPool = buildSimplePool(this.objectFactory);
                break;

            case UNBOUNDED:
                objectPool = buildUnboundedPool(this.objectFactory);
                break;

            default:
                throw new IllegalStateException("unexpected type: " + this.type);
        }

        if (isRegisterShutdownHook())
        {
            Runtime.getRuntime().addShutdownHook(new Thread(objectPool::shutdown, objectPool.getClass().getSimpleName()));
        }

        return objectPool;
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
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    private ObjectPool<T> buildCommonsPool(final ObjectFactory<T> objectFactory)
    {
        CommonsObjectFactoryAdapter<T> objectFactoryAdapter = new CommonsObjectFactoryAdapter<>(objectFactory);

        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
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

        ObjectPool<T> objectPool = new CommonsObjectPoolAdapter<>(config, objectFactoryAdapter);

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
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    private ObjectPool<T> buildErasoftPool(final ObjectFactory<T> objectFactory)
    {
        ErasoftObjectFactoryAdapter<T> objectFactoryAdapter = new ErasoftObjectFactoryAdapter<>(objectFactory);

        PoolSettings<T> settings = new PoolSettings<>(objectFactoryAdapter);
        settings.max(getMaxSize() <= 0 ? DEFAULT_MAX_SIZE : getMaxSize());
        settings.maxIdle(settings.max());
        settings.maxWait(getMaxWait() <= 0 ? 0 : getMaxWait() / 1000);
        settings.min(getCoreSize() <= 0 ? DEFAULT_CORE_SIZE : getCoreSize());
        settings.validateWhenReturn(isValidateOnReturn());

        ObjectPool<T> objectPool = new ErasoftObjectPoolAdapter<>(settings);

        return objectPool;
    }

    /**
     * Liefert einen {@link RoundRobinPool}.<br>
     * Verwendete Attribute:
     * <ul>
     * <li>{@link #maxSize}
     * </ul>
     *
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    private ObjectPool<T> buildRoundRobinPool(final ObjectFactory<T> objectFactory)
    {
        int maxSize = getMaxSize() <= 0 ? RoundRobinPool.DEFAULT_SIZE : getMaxSize();

        RoundRobinPool<T> objectPool = new RoundRobinPool<>(maxSize, objectFactory);

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
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    private ObjectPool<T> buildSimplePool(final ObjectFactory<T> objectFactory)
    {
        int coreSize = getCoreSize() <= 0 ? DEFAULT_CORE_SIZE : getCoreSize();
        int maxSize = getMaxSize() <= 0 ? DEFAULT_MAX_SIZE : getMaxSize();

        SimpleObjectPool<T> objectPool = new SimpleObjectPool<>(coreSize, maxSize, objectFactory);

        return objectPool;
    }

    /**
     * Liefert einen {@link UnboundedObjectPool}.<br>
     *
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPool}
     */
    private ObjectPool<T> buildUnboundedPool(final ObjectFactory<T> objectFactory)
    {
        UnboundedObjectPool<T> objectPool = new UnboundedObjectPool<>(objectFactory);

        return objectPool;
    }

    /**
     * @param objectFactory {@link ObjectFactory}
     * @return {@link ObjectPoolBuilder}
     */
    public ObjectPoolBuilder<T> objectFactory(final ObjectFactory<T> objectFactory)
    {
        this.objectFactory = objectFactory;

        return this;
    }

    /**
     * @param type {@link ObjectPoolType}
     * @return {@link ObjectPoolBuilder}
     */
    public ObjectPoolBuilder<T> type(final ObjectPoolType type)
    {
        this.type = type;

        return this;
    }
}
