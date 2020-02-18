/**
 * Created: 27.01.2014
 */

package de.freese.base.pool.commons;

import java.util.Objects;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import de.freese.base.pool.factory.ObjectFactory;

/**
 * {@link PooledObjectFactory}-Adapter für {@link ObjectFactory}.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class CommonsObjectFactoryAdapter<T> implements PooledObjectFactory<T>
{
    /**
     *
     */
    private final ObjectFactory<T> objectFactory;

    /**
     * Erstellt ein neues {@link CommonsObjectFactoryAdapter} Object.
     *
     * @param objectFactory {@link ObjectFactory}
     */
    public CommonsObjectFactoryAdapter(final ObjectFactory<T> objectFactory)
    {
        super();

        this.objectFactory = Objects.requireNonNull(objectFactory, "objectFactory required");
    }

    /**
     * @see org.apache.commons.pool2.PooledObjectFactory#activateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void activateObject(final PooledObject<T> p) throws Exception
    {
        this.objectFactory.activate(p.getObject());
    }

    /**
     * @see org.apache.commons.pool2.PooledObjectFactory#destroyObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void destroyObject(final PooledObject<T> p) throws Exception
    {
        this.objectFactory.destroy(p.getObject());
    }

    /**
     * @see org.apache.commons.pool2.PooledObjectFactory#makeObject()
     */
    @Override
    public PooledObject<T> makeObject() throws Exception
    {
        return new DefaultPooledObject<>(this.objectFactory.create());
    }

    /**
     * @see org.apache.commons.pool2.PooledObjectFactory#passivateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void passivateObject(final PooledObject<T> p) throws Exception
    {
        this.objectFactory.passivate(p.getObject());
    }

    /**
     * @see org.apache.commons.pool2.PooledObjectFactory#validateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public boolean validateObject(final PooledObject<T> p)
    {
        return this.objectFactory.validate(p.getObject());
    }
}
