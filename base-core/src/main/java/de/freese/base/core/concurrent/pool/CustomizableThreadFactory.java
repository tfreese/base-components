package de.freese.base.core.concurrent.pool;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class CustomizableThreadFactory implements ThreadFactory
{
    /**
     *
     */
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    /**
     *
     */
    private final String namePrefix;

    /**
     * Thread.NORM_PRIORITY
     */
    private final int priority;

    /**
     *
     */
    private final ThreadGroup threadGroup;

    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Erzeugt eine neue Instanz von {@link CustomizableThreadFactory}
     *
     * @param namePrefix String
     * @param priority int
     */
    public CustomizableThreadFactory(final String namePrefix, final int priority)
    {
        super();

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required");

        if ((priority < Thread.MIN_PRIORITY) || (priority > Thread.MAX_PRIORITY))
        {
            throw new IllegalArgumentException("priority must be >= Thread.MIN_PRIORITY and <= Thread.MAX_PRIORITY");
        }

        this.priority = priority;

        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm != null) ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();

        POOL_NUMBER.getAndIncrement();
    }

    /**
     * @param namePrefix String
     * @param poolNumber int
     * @param threadNumber int
     * @return String
     */
    protected String createThreadName(final String namePrefix, final int poolNumber, final int threadNumber)
    {
        // Klassisch: java.util.concurrent.Executors.DefaultThreadFactory.newThread(Runnable)
        // String threadName = String.format("pool-%02d-thread-%02d", poolNumber, threadNumber);

        String threadName = String.format("%s-%02d", namePrefix, threadNumber);

        return threadName;
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable task)
    {
        String threadName = createThreadName(this.namePrefix, POOL_NUMBER.get(), this.threadNumber.getAndIncrement());

        Thread thread = new Thread(this.threadGroup, task, threadName, 0);

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != this.priority)
        {
            thread.setPriority(this.priority);
        }

        return thread;
    }
}