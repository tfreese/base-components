/**
 * Created: 10.04.2020
 */

package de.freese.base.pool;

import java.lang.reflect.ParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private Class<T> objectClazz = null;

    /**
     * Erstellt ein neues {@link AbstractObjectPool} Object.
     */
    public AbstractObjectPool()
    {
        super();
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
     * @param objectClazz Class<T>
     */
    protected void setObjectClazz(final Class<T> objectClazz)
    {
        this.objectClazz = objectClazz;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        String objectClazzName = getObjectClazz().getSimpleName();

        getLogger().info("Close Pool<{}> with {} idle and {} aktive Objects", objectClazzName, getNumIdle(), getNumActive());
    }

    /**
     * Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !<br>
     * Z.B.: public class MyObjectPool extends AbstractObjectPool<Integer><br>
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
