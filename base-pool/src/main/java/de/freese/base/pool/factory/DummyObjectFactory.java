/**
 * Created: 11.04.2020
 */

package de.freese.base.pool.factory;

/**
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public final class DummyObjectFactory<T> implements ObjectFactory<T>
{
    /**
     * Erstellt ein neues {@link DummyObjectFactory} Object.
     */
    public DummyObjectFactory()
    {
        super();
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#activate(java.lang.Object)
     */
    @Override
    public void activate(final T t)
    {
        // NOOP
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#create()
     */
    @Override
    public T create()
    {
        throw new UnsupportedOperationException("create not implemented");
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#destroy(java.lang.Object)
     */
    @Override
    public void destroy(final T t)
    {
        // NOOP
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#passivate(java.lang.Object)
     */
    @Override
    public void passivate(final T t)
    {
        // NOOP
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#validate(java.lang.Object)
     */
    @Override
    public boolean validate(final T t)
    {
        return true;
    }
}
