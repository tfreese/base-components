package de.freese.base.core.exception;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = -8095454479589758508L;
    /**
     * Map verhindert Exceptions mit gleichen Messages.
     */
    private final Map<String, Throwable> throwables = new LinkedHashMap<>();

    public void addThrowable(final Throwable th)
    {
        this.throwables.put(th.getMessage(), th);
    }

    public Throwable getFirstThrowable()
    {
        return getThrowables().get(0);
    }

    public Throwable getLastThrowable()
    {
        List<Throwable> ths = getThrowables();

        return ths.get(ths.size() - 1);
    }

    public List<Throwable> getThrowables()
    {
        return new ArrayList<>(this.throwables.values());
    }

    public boolean isEmpty()
    {
        return this.throwables.isEmpty();
    }
}
