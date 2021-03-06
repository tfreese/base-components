/**
 * Created: 10.04.2020
 */

package de.freese.base.pool;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.pool.factory.DefaultObjectFactory;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * Basis-Implementierung eones {@link ObjectPool}s.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T>
{
    /**
    *
    */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private Class<T> objectClazz;

    /**
    *
    */
    private final ObjectFactory<T> objectFactory;

    /**
    *
    */
    private Queue<T> queue;

    /**
     * Erstellt ein neues {@link AbstractObjectPool} Object.
     */
    protected AbstractObjectPool()
    {
        this(new DefaultObjectFactory<>(() -> null));
    }

    /**
     * Erstellt ein neues {@link AbstractObjectPool} Object.
     *
     * @param objectFactory {@link ObjectFactory}
     */
    protected AbstractObjectPool(final ObjectFactory<T> objectFactory)
    {
        super();

        this.objectFactory = Objects.requireNonNull(objectFactory, "objectFactory required");
    }

    /**
     * @return {@link ReentrantLock}
     */
    protected ReentrantLock getLock()
    {
        return this.lock;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return Class<T>
     */
    @SuppressWarnings("unchecked")
    protected synchronized Class<T> getObjectClazz()
    {
        if (this.objectClazz == null)
        {
            try
            {
                this.objectClazz = tryDetermineObjectClazz();
            }
            catch (ClassCastException ccex)
            {
                T object = borrowObject();

                if (object != null)
                {
                    this.objectClazz = (Class<T>) object.getClass();

                    returnObject(object);
                }
            }
        }

        return this.objectClazz;
    }

    /**
     * @return {@link ObjectFactory}
     */
    protected ObjectFactory<T> getObjectFactory()
    {
        return this.objectFactory;
    }

    /**
     * @return {@link Queue}
     */
    protected Queue<T> getQueue()
    {
        return this.queue;
    }

    /**
     * @param objectClazz Class<T>
     */
    protected void setObjectClazz(final Class<T> objectClazz)
    {
        this.objectClazz = objectClazz;
    }

    /**
     * @param queue {@link Queue}
     */
    protected void setQueue(final Queue<T> queue)
    {
        this.queue = Objects.requireNonNull(queue, "queue required");
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        String objectClazzName = getObjectClazz().getSimpleName();

        getLogger().info("Close Pool<{}> with {} idle and {} aktive Objects", objectClazzName, getNumIdle(), getNumActive());

        if (getQueue() != null)
        {
            while (!getQueue().isEmpty())
            {
                T object = getQueue().poll();

                if (object == null)
                {
                    continue;
                }

                getObjectFactory().destroy(object);
            }

            // for (Iterator<T> iterator = getQueue().iterator(); iterator.hasNext();)
            // {
            // T object = iterator.next();
            //
            // if (object == null)
            // {
            // continue;
            // }
            //
            // getObjectFactory().destroy(object);
            //
            // iterator.remove();
            // }
        }
    }

    /**
     * Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !<br>
     * Z.B.:
     *
     * <pre>
     * {@code
     * public class MyObjectPool extends AbstractObjectPool<Integer>
     * }
     * </pre>
     *
     * <br>
     *
     * @return Class
     * @throws ClassCastException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> tryDetermineObjectClazz() throws ClassCastException
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
