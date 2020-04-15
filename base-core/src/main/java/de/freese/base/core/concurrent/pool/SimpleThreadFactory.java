package de.freese.base.core.concurrent.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 * @see Executors#defaultThreadFactory
 */
public class SimpleThreadFactory implements ThreadFactory
{
    /**
     *
     */
    private final ThreadGroup threadGroup;

    /**
     *
     */
    private final String threadNamePattern;

    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     *
     */
    private final int threadPriority;

    /**
     * Erzeugt eine neue Instanz von {@link SimpleThreadFactory}
     *
     * <pre>
     * Defaults:
     * - namingPattern = thread-%02d
     * - threadPriority = Thread.NORM_PRIORITY
     * </pre>
     */
    public SimpleThreadFactory()
    {
        this("thread-%02d", Thread.NORM_PRIORITY);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SimpleThreadFactory}
     *
     * @param threadNamePattern String; Beispiel: thread-%02d
     * @param threadPriority int
     */
    public SimpleThreadFactory(final String threadNamePattern, final int threadPriority)
    {
        super();

        if ((threadNamePattern == null) || threadNamePattern.strip().isEmpty())
        {
            throw new IllegalArgumentException("threadNamePattern required");
        }

        if ((threadPriority < Thread.MIN_PRIORITY) || (threadPriority > Thread.MAX_PRIORITY))
        {
            throw new IllegalArgumentException("priority must be >= Thread.MIN_PRIORITY and <= Thread.MAX_PRIORITY");
        }

        this.threadNamePattern = threadNamePattern;
        this.threadPriority = threadPriority;

        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();

    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable task)
    {
        final String threadName = String.format(this.threadNamePattern, this.threadNumber.getAndIncrement());

        final Thread thread = new Thread(this.threadGroup, task, threadName, 0);

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != this.threadPriority)
        {
            thread.setPriority(this.threadPriority);
        }

        return thread;
    }
}