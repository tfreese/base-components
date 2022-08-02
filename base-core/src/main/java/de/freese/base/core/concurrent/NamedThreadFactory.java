// Created: 10.09.2020
package de.freese.base.core.concurrent;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class NamedThreadFactory implements ThreadFactory
{
    /**
     *
     */
    private final boolean daemon;
    /**
     *
     */
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
    /**
     *
     */
    private final String namePattern;
    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Erstellt ein neues {@link NamedThreadFactory} Object.
     *
     * <pre>
     * Defaults:
     * - daemon = true
     * </pre>
     *
     * @param namePattern String; Example: "thread-%d"
     */
    public NamedThreadFactory(final String namePattern)
    {
        this(namePattern, true);
    }

    /**
     * Erstellt ein neues {@link NamedThreadFactory} Object.
     *
     * @param namePattern String; Example: "thread-%d"
     * @param daemon boolean
     */
    public NamedThreadFactory(final String namePattern, final boolean daemon)
    {
        super();

        this.namePattern = Objects.requireNonNull(namePattern, "namePattern required");
        this.daemon = daemon;
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = this.defaultThreadFactory.newThread(r);

        String threadName = String.format(this.namePattern, this.threadNumber.getAndIncrement());
        thread.setName(threadName);

        thread.setDaemon(this.daemon);

        return thread;
    }
}
