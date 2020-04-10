/**
 * Created: 26.01.2014
 */

package de.freese.base.pool.commons;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import de.freese.base.pool.AbstractObjectPool;
import de.freese.base.pool.ObjectPool;

/**
 * {@link GenericObjectPool}-Adapter für {@link ObjectPool}.
 *
 * @author Thomas Freese
 * @param <T> Konkreter ObjectTyp
 */
public class CommonsObjectPoolAdapter<T> extends AbstractObjectPool<T>
{
    // /**
    // * @return {@link GenericObjectPoolConfig}
    // */
    // protected static GenericObjectPoolConfig getDefaultConfig()
    // {
    // GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    // config.setMaxTotal(-1);
    // config.setMaxIdle(-1);
    // config.setMaxWaitMillis(-1);
    // config.setMinIdle(0);
    // config.setTestOnBorrow(false);
    // config.setTestOnReturn(false);
    // config.setTestWhileIdle(false);
    // config.setLifo(true);
    // config.setMinEvictableIdleTimeMillis(-1);
    // config.setNumTestsPerEvictionRun(0);
    // config.setSoftMinEvictableIdleTimeMillis(-1);
    // config.setTimeBetweenEvictionRunsMillis(-1);
    // config.setBlockWhenExhausted(false);
    //
    // return config;
    // }

    /**
     *
     */
    private final GenericObjectPool<T> objectPool;

    // /**
    // * Erstellt ein neues {@link CommonsObjectPoolAdapter} Object.
    // *
    // * @param objectFactory {@link PooledObjectFactory}
    // */
    // public CommonsObjectPoolAdapter(final PooledObjectFactory<T> objectFactory)
    // {
    // this(objectFactory, getDefaultConfig());
    // }

    /**
     * Erstellt ein neues {@link CommonsObjectPoolAdapter} Object.
     *
     * @param config {@link GenericObjectPoolConfig}
     * @param objectFactory {@link PooledObjectFactory}
     */
    public CommonsObjectPoolAdapter(final GenericObjectPoolConfig<T> config, final PooledObjectFactory<T> objectFactory)
    {
        super();

        this.objectPool = new GenericObjectPool<>(objectFactory, config);
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        T object = null;

        try
        {
            object = this.objectPool.borrowObject();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return object;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.objectPool.getNumActive();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return this.objectPool.getNumIdle();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        try
        {
            this.objectPool.returnObject(object);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        super.shutdown();

        try
        {
            this.objectPool.clear();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            this.objectPool.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
