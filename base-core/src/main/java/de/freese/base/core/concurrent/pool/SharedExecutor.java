// Created: 03.04.2021
package de.freese.base.core.concurrent.pool;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Executor} mit einer bestimmten Thread-Anzahl, die er nur von einem anderen {@link Executor} verwenden kann.
 *
 * @author Thomas Freese
 */
public class SharedExecutor implements Executor
{
    /**
     * @author Thomas Freese
     */
    protected final class SharedRunnable implements Runnable
    {
        /**
        *
        */
        private final Runnable delegate;

        /**
         * Erstellt ein neues {@link SharedRunnable} Object.
         *
         * @param delegate {@link Runnable}
         */
        protected SharedRunnable(final Runnable delegate)
        {
            super();

            this.delegate = Objects.requireNonNull(delegate, "delegate rrequired");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try
            {
                this.delegate.run();
            }
            finally
            {
                taskFinished();
            }
        }
    }

    /**
     *
     */
    private int activeTasks;

    /**
     *
     */
    private final Executor delegate;

    /**
     *
     */
    private final BlockingQueue<Runnable> delegateQueue;

    /**
    *
    */
    private final Lock lock = new ReentrantLock();

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final int maxThreads;

    /**
     * Erstellt ein neues {@link SharedExecutor} Object.
     *
     * @param delegate {@link Executor}
     * @param maxThreads int
     */
    public SharedExecutor(final Executor delegate, final int maxThreads)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (maxThreads <= 0)
        {
            throw new IllegalArgumentException(String.format("maxThreads is %d but must >= 1", maxThreads));
        }

        this.maxThreads = maxThreads;

        this.delegateQueue = new LinkedBlockingQueue<>();
    }

    /**
     *
     */
    protected void decrementActiveTasks()
    {
        this.activeTasks--;
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    @Override
    public void execute(final Runnable command)
    {
        getLock().lock();

        try
        {
            if (getActiveTasks() < getMaxThreads())
            {
                incrementActiveTasks();

                getDelegate().execute(new SharedRunnable(command));

                getLogger().debug("ActiveTasks={}; execute task", getActiveTasks());
            }
            else
            {
                getDelegateQueue().offer(new SharedRunnable(command));

                getLogger().debug("ActiveTasks={}; queue task", getActiveTasks());
            }
        }
        finally
        {
            getLock().unlock();
        }
    }

    /**
     * @return int
     */
    public int getActiveTasks()
    {
        return this.activeTasks;
    }

    /**
     * @return Executor
     */
    protected Executor getDelegate()
    {
        return this.delegate;
    }

    /**
     * @return BlockingQueue<Runnable>
     */
    protected BlockingQueue<Runnable> getDelegateQueue()
    {
        return this.delegateQueue;
    }

    /**
     * @return {@link Lock}
     */
    protected Lock getLock()
    {
        return this.lock;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return int
     */
    public int getMaxThreads()
    {
        return this.maxThreads;
    }

    /**
     *
     */
    protected void incrementActiveTasks()
    {
        this.activeTasks++;
    }

    /**
    *
    */
    protected void taskFinished()
    {
        getLock().lock();

        getLogger().debug("ActiveTasks={}; task finished", getActiveTasks());

        try
        {
            decrementActiveTasks();

            while ((getActiveTasks() < getMaxThreads()) && !getDelegateQueue().isEmpty())
            {
                incrementActiveTasks();

                getDelegate().execute(getDelegateQueue().poll());

                getLogger().debug("ActiveTasks={}; execute queued task", getActiveTasks());
            }
        }
        finally
        {
            getLock().unlock();
        }
    }
}
