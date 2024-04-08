package de.freese.base.core.exception;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collecting multiple {@link Exception}.<br>
 *
 * @author Thomas Freese
 */
public class ThrowableTracker {
    private final Map<String, Throwable> throwables = new LinkedHashMap<>();

    public void addThrowable(final Throwable th) {
        this.throwables.put(th.getMessage(), th);
    }

    public Throwable getFirstThrowable() {
        return getThrowables().get(0);
    }

    public Throwable getLastThrowable() {
        final List<Throwable> ths = getThrowables();

        return ths.get(ths.size() - 1);
    }

    public List<Throwable> getThrowables() {
        return new ArrayList<>(this.throwables.values());
    }

    public boolean isEmpty() {
        return this.throwables.isEmpty();
    }
}
