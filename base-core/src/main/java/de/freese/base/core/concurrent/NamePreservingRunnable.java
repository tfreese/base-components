package de.freese.base.core.concurrent;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Runnable} Wrapper for changing the name of the current Thread.<br>
 * After execution, the name is restored.
 *
 * @author Thomas Freese
 */
public class NamePreservingRunnable implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NamePreservingRunnable.class);

    private final Runnable runnable;

    private final String runnableName;

    public NamePreservingRunnable(final Runnable runnable, final String runnableName)
    {
        super();

        this.runnable = Objects.requireNonNull(runnable, "runnable required");
        this.runnableName = Objects.requireNonNull(runnableName, "runnableName required");
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        final Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();

        setName(currentThread, this.runnableName);

        try
        {
            this.runnable.run();
        }
        finally
        {
            setName(currentThread, oldName);
        }
    }

    /**
     * Ändert den Namen des Threads.<br>
     * Eine auftretende {@link SecurityException} wird als Warning geloggt.
     */
    private void setName(final Thread thread, final String name)
    {
        try
        {
            thread.setName(name);
        }
        catch (SecurityException sex)
        {
            LOGGER.warn("Failed to set the thread name.", sex);
        }
    }
}
