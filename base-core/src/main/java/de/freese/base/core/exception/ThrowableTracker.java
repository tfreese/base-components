/**
 *
 */
package de.freese.base.core.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse sammelt Exceptions ein, um diese geschlossen zu verarbeiten.<br>
 *
 * @author Thomas Freese
 */
public class ThrowableTracker implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -8095454479589758508L;

    /**
     * Map verhindert Exceptions mit gleichen Messages.
     */
    private Map<String, Throwable> throwables = new LinkedHashMap<>();

    /**
     * Hinzufuegen eines {@link Throwable}.
     *
     * @param th {@link Throwable}
     */
    public void addThrowable(final Throwable th)
    {
        this.throwables.put(th.getMessage(), th);
    }

    /**
     * Liefert den ersten {@link Throwable}.
     *
     * @return {@link Throwable}.
     */
    public Throwable getFirstThrowable()
    {
        return getThrowables().get(0);
    }

    /**
     * Liefert den letzten {@link Throwable}.
     *
     * @return {@link Throwable}.
     */
    public Throwable getLastThrowable()
    {
        List<Throwable> ths = getThrowables();

        return ths.get(ths.size() - 1);
    }

    /**
     * Liefert die Liste der {@link Throwable} in der Reihenfolge in der diese aufgetreten sind.
     *
     * @return {@link List}
     */
    public List<Throwable> getThrowables()
    {
        return new ArrayList<>(this.throwables.values());
    }

    /**
     * @return boolean
     * @see List#isEmpty()
     */
    public boolean isEmpty()
    {
        return this.throwables.isEmpty();
    }
}
