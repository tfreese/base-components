// Created: 10.09.2020
package de.freese.base.core.concurrent.pool;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class SimpleThreadFactory implements ThreadFactory
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
     * Erstellt ein neues {@link SimpleThreadFactory} Object.
     *
     * <pre>
     * Defaults:
     * - namePattern = thread-%02d
     * - daemon = true
     * </pre>
     */
    public SimpleThreadFactory()
    {
        this("thread-%02d", true);
    }

    /**
     * Erstellt ein neues {@link SimpleThreadFactory} Object.
     *
     * @param namePattern String; Example: "thread-%02d"
     * @param daemon boolean
     */
    public SimpleThreadFactory(final String namePattern, final boolean daemon)
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
