// Created: 03.04.2021
package de.freese.base.core.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
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
            final Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();

            setName(currentThread, nextThreadName());

            try
            {
                this.delegate.run();
            }
            finally
            {
                setName(currentThread, oldName);

                taskFinished();
            }
        }

        /**
         * Ändert den Namen des Threads.<br>
         * Eine auftretende {@link SecurityException} wird als Warning geloggt.
         *
         * @param thread {@link Thread}
         * @param name String
         */
        private void setName(final Thread thread, final String name)
        {
            try
            {
                thread.setName(name);
            }
            catch (SecurityException sex)
            {
                getLogger().warn("Failed to set the thread name.", sex);
            }
        }
    }

    /**
    *
    */
    private static final AtomicInteger SHAREDPOOLNUMBER = new AtomicInteger(1);

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
     *
     */
    private int nameIndex;

    /**
    *
    */
    private final List<String> threadNames;

    /**
     * Erstellt ein neues {@link SharedExecutor} Object.
     *
     * @param delegate {@link Executor}
     * @param maxThreads int
     */
    public SharedExecutor(final Executor delegate, final int maxThreads)
    {
        this(delegate, maxThreads, "sharedpool-" + SHAREDPOOLNUMBER.getAndIncrement() + "-thread-%d");
    }

    /**
     * Erstellt ein neues {@link SharedExecutor} Object.
     *
     * @param delegate {@link Executor}
     * @param maxThreads int
     * @param namePattern String; Example: "thread-%02d"
     */
    public SharedExecutor(final Executor delegate, final int maxThreads, final String namePattern)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (maxThreads <= 0)
        {
            throw new IllegalArgumentException(String.format("maxThreads is %d but must >= 1", maxThreads));
        }

        this.maxThreads = maxThreads;

        this.delegateQueue = new LinkedBlockingQueue<>();

        this.threadNames = new ArrayList<>(maxThreads);

        for (int i = 0; i < maxThreads; i++)
        {
            this.threadNames.add(String.format(namePattern, i + 1));
        }
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
     * @return String
     */
    protected synchronized String nextThreadName()
    {
        String name = this.threadNames.get(this.nameIndex);
        this.nameIndex++;

        if (this.nameIndex >= this.threadNames.size())
        {
            this.nameIndex = 0;
        }

        return name;
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
