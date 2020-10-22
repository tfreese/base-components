// Created: 04.10.2020
package de.freese.base.core.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Die {@link #get()}-Methode blockiert so lange bis {@link #setResponse(Object)} aufgerufen wird.
 *
 * @author Thomas Freese
 * @param <T> Type of Response
 */
public class SyncFuture<T> implements Future<T>
{
    /**
     *
     */
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     *
     */
    private T response;

    /**
     *
     */
    private final long startTime = System.currentTimeMillis();

    /**
     * Erstellt ein neues {@link SyncFuture} Object.
     */
    public SyncFuture()
    {
        super();
    }

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

        return this.response;
    }

    /**
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        if (this.latch.await(timeout, unit))
        {
            return this.response;
        }

        return null;
    }

    /**
     * @return long
     */
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
        return this.response != null;
    }

    /**
     * @param response Object
     */
    public void setResponse(final T response)
    {
        this.response = response;

        this.latch.countDown();
    }
}
