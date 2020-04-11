/**
 * Created: 17.10.2017
 */

package de.freese.base.pool.factory;

import de.freese.base.pool.ObjectPool;

/**
 * Interface zur Kapselung der ObjectFactory eines {@link ObjectPool}.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public interface ObjectFactory<T>
{
    /**
     * Wird vor {@link ObjectPool#borrowObject()} aufgerufen.
     *
     * @param t Object
     */
    public void activate(T t);

    /**
     * Erzeugt ein Objekt.
     *
     * @return Object
     */
    public T create();

    /**
     * Zerstört ein Objekt, wenn es aus dem Pool entfernt wird oder {@link #validate(Object)} false ist.
     *
     * @param t Object
     */
    public void destroy(T t);

    /**
     * Wird nach {@link ObjectPool#returnObject(Object)} aufgerufen.
     *
     * @param t Object
     */
    public void passivate(T t);

    /**
     * Wird vor {@link #activate(Object)} aufgerufen.
     *
     * @param t Object
     * @return boolean
     */
    public boolean validate(final T t);
}
