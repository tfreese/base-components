// Created: 04.10.2020
package de.freese.base.core.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@link #get()}-Method is blocking until {@link #setResult(Object)} is called.
 *
 * @author Thomas Freese
 */
public class SyncFuture<T> implements Future<T>
{
    private final CountDownLatch latch = new CountDownLatch(1);

    private final long startTime = System.currentTimeMillis();

    private T result;

    /**
     * @see java.util.concurrent.Future#cancel(boolean)
     */
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning)
    {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#get()
     */
    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        this.latch.await();

        return this.result;
    }

    /**
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        if (this.latch.await(timeout, unit))
        {
            return this.result;
        }

        return null;
    }

    public long getStartTime()
    {
        return this.startTime;
    }

    /**
     * @see java.util.concurrent.Future#isCancelled()
     */
    @Override
    public boolean isCancelled()
    {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isDone()
     */
    @Override
    public boolean isDone()
    {
        return this.result != null;
    }

    public void setResult(final T result)
    {
        this.result = result;

        this.latch.countDown();
    }
}
