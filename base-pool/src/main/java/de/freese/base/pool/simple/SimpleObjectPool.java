/**
 * Created: 06.02.2014
 */
package de.freese.base.pool.simple;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.pool.ObjectPool;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * Einfache Implementierung eines ObjektPools.<br>
 * Verhalten<br>
 * borrowObject: Validate true -> Activate -> return<br>
 * borrowObject: Validate false -> Destroy -> create new<br>
 * returnObject: Passivate
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class SimpleObjectPool<T> implements ObjectPool<T>
{
    /**
     * Konfiguration des Pools.
     *
     * @author Thomas Freese
     */
    public static class PoolConfig
    {
        /**
         *
         */
        private int max = 10;

        /**
         *
         */
        private int min = 1;

        /**
         * Erzeugt eine neue Instanz von {@link PoolConfig}.
         */
        public PoolConfig()
        {
            super();
        }

        /**
         * @return int
         */
        public int getMax()
        {
            return this.max;
        }

        /**
         * @return int
         */
        public int getMin()
        {
            return this.min;
        }

        /**
         * @param max int
         * @return {@link PoolConfig}
         */
        public PoolConfig max(final int max)
        {
            this.max = max;

            if ((this.max > 0) && (this.max < this.min))
            {
                min(this.max);
            }

            return this;
        }

        /**
         * @param min int
         * @return {@link PoolConfig}
         */
        public PoolConfig min(final int min)
        {
            this.min = min;

            if ((this.max > 0) && (this.min > this.max))
            {
                max(this.min);
            }

            return this;
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleObjectPool.class);

    /**
     *
     */
    private final Set<T> busy = new HashSet<>();

    /**
     *
     */
    private final PoolConfig config;

    /**
     *
     */
    private final Queue<T> idle = new LinkedList<>();

    /**
     *
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private final ObjectFactory<T> objectFactory;

    /**
     * Erstellt ein neues {@link SimpleObjectPool} Object.
     *
     * @param config {@link PoolConfig}
     * @param objectFactory {@link ObjectFactory}
     */
    public SimpleObjectPool(final PoolConfig config, final ObjectFactory<T> objectFactory)
    {
        super();

        this.config = Objects.requireNonNull(config, "config required");
        this.objectFactory = Objects.requireNonNull(objectFactory, "objectFactory required");

        init();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public T borrowObject()
    {
        this.lock.lock();

        try
        {
            T object = null;

            while (object == null)
            {
                object = this.idle.poll();

                if ((object == null) && (getTotalSize() < getMax()))
                {
                    object = createObject();
                }
                else
                {
                    if (!getObjectFactory().validate(object))
                    {
                        getObjectFactory().destroy(object);
                        object = null;
                        continue;
                    }
                }

                if (object == null)
                {
                    throw new NoSuchElementException("pool exhausted");
                }

                getObjectFactory().activate(object);

                this.busy.add(object);
            }

            return object;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Erzeugt ein Objekt.
     *
     * @return Object
     */
    private T createObject()
    {
        T object = null;

        while (object == null)
        {
            object = getObjectFactory().create();

            if (!getObjectFactory().validate(object))
            {
                getObjectFactory().destroy(object);
                object = null;
                continue;
            }
        }

        return object;
    }

    /**
     * @return int
     */
    public int getMax()
    {
        return this.config.getMax();
    }

    /**
     * @return int
     */
    public int getMin()
    {
        return this.config.getMin();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.busy.size();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return this.idle.size();
    }

    /**
     * @return {@link ObjectFactory}
     */
    protected ObjectFactory<T> getObjectFactory()
    {
        return this.objectFactory;
    }

    /**
     * Füllt den Pool bis zum min-Wert.
     */
    private void init()
    {
        this.lock.lock();

        try
        {
            // System.out.println("fill: " + Thread.currentThread().getName());
            IntStream.range(0, getMin()).mapToObj(i -> {
                return createObject();
            }).forEach(this.idle::offer);

            // System.out.println(getClass().getGenericSuperclass());
            // System.out.println((((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        this.lock.lock();

        try
        {
            getObjectFactory().passivate(object);

            this.busy.remove(object);
            this.idle.offer(object);
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        this.lock.lock();

        try
        {
            this.idle.addAll(this.busy);
            this.busy.clear();

            String objectClassName = createObject().getClass().getSimpleName();
            LOGGER.info("Close Pool<{}> with {} idle and {} aktive Objects", objectClassName, getNumIdle(), getNumActive());

            while (this.idle.size() > 0)
            {
                T object = this.idle.poll();

                if (object == null)
                {
                    continue;
                }

                getObjectFactory().destroy(object);
            }

            this.config.min(0);
            this.config.max(0);
        }
        finally
        {
            this.lock.unlock();
        }
    }
}
