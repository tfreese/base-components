/**
 * Created: 17.10.2017
 */

package de.freese.base.pool.factory;

import de.freese.base.pool.ObjectPool;

/**
 * Interface zur Kapselung der ObjectFactory eines {@link ObjectPool}.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter ObjectTyp
 */
public interface ObjectFactory<T>
{
    /**
     * Wird aufgerufen nachdem das Objekt passiviert wurde und bevor es dem Caller geliefert wird.
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
     * Zerstört das Objekt, wenn es aus dem Pool entfernt wird.
     *
     * @param t Object
     */
    public void destroy(T t);

    /**
     * Wird aufgerufen, wenn das Objekt in den Pool zurück kehrt.
     *
     * @param t Object
     */
    public void passivate(T t);

    /**
     * Wird aufgerufen VOR {@link #activate(Object)} und NACH {@link #passivate(Object)}.
     *
     * @param t Object
     * @return boolean
     */
    public boolean validate(final T t);
}
