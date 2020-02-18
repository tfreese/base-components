/**
 * Created: 27.01.2014
 */

package de.freese.base.pool.erasoft;

import java.util.Objects;
import de.freese.base.pool.factory.ObjectFactory;
import nf.fr.eraasoft.pool.PoolException;
import nf.fr.eraasoft.pool.PoolableObject;

/**
 * {@link PoolableObject}-Adapter für {@link ObjectFactory}.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class ErasoftObjectFactoryAdapter<T> implements PoolableObject<T>
{
    /**
     *
     */
    private final ObjectFactory<T> objectFactory;

    /**
     * Erstellt ein neues {@link ErasoftObjectFactoryAdapter} Object.
     *
     * @param objectFactory {@link ObjectFactory}
     */
    public ErasoftObjectFactoryAdapter(final ObjectFactory<T> objectFactory)
    {
        super();

        this.objectFactory = Objects.requireNonNull(objectFactory, "objectFactory required");
    }

    /**
     * @see nf.fr.eraasoft.pool.PoolableObject#activate(java.lang.Object)
     */
    @Override
    public void activate(final T t)
    {
        this.objectFactory.activate(t);
    }

    /**
     * @see nf.fr.eraasoft.pool.PoolableObject#destroy(java.lang.Object)
     */
    @Override
    public void destroy(final T t)
    {
        this.objectFactory.destroy(t);
    }

    /**
     * @see nf.fr.eraasoft.pool.PoolableObject#make()
     */
    @Override
    public T make() throws PoolException
    {
        return this.objectFactory.create();
    }

    /**
     * @see nf.fr.eraasoft.pool.PoolableObject#passivate(java.lang.Object)
     */
    @Override
    public void passivate(final T t)
    {
        this.objectFactory.passivate(t);
    }

    /**
     * @see nf.fr.eraasoft.pool.PoolableObject#validate(java.lang.Object)
     */
    @Override
    public boolean validate(final T t)
    {
        return this.objectFactory.validate(t);
    }
}
