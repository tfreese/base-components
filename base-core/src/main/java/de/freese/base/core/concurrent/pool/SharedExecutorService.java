// Created: 04.04.2021
package de.freese.base.core.concurrent.pool;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link ExecutorService} mit einer bestimmten Thread-Anzahl, die er nur von einem anderen {@link ExecutorService} verwenden kann.
 *
 * @author Thomas Freese
 */
public class SharedExecutorService extends SharedExecutor implements ExecutorService
{
    /**
     * Erstellt ein neues {@link SharedExecutorService} Object.
     *
     * @param delegate {@link ExecutorService}
     * @param maxThreads int
     */
    public SharedExecutorService(final ExecutorService delegate, final int maxThreads)
    {
        super(delegate, maxThreads);
    }

    /**
     * @see java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException
    {
        throw new UnsupportedOperationException("Use Delegted ExecutorService instead");
    }

    /**
     * @param <T> Type
     * @param futures {@link List}
     */
    protected <T> void cancelAll(final List<Future<T>> futures)
    {
        cancelAll(futures, 0);
    }

    /**
     * Cancels all futures with index at least j.
     *
     * @param futures {@link List}
     * @param j int
     * @param <T> Type
     */
    protected <T> void cancelAll(final List<Future<T>> futures, int j)
    {
        for (int size = futures.size(); j < size; j++)
        {
            futures.get(j).cancel(true);
        }
    }

    /**
     * @see de.freese.base.core.concurrent.pool.SharedExecutor#getDelegate()
     */
    @Override
    protected ExecutorService getDelegate()
    {
        return (ExecutorService) super.getDelegate();
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
     */
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        Objects.requireNonNull(tasks, "tasks required");

        List<Future<T>> futures = new ArrayList<>(tasks.size());

        try
        {
            for (Callable<T> task : tasks)
            {
                RunnableFuture<T> future = newTaskFor(task);
                futures.add(future);
                execute(future);
            }

            for (Future<T> future : futures)
            {
                if (!future.isDone())
                {
                    try
                    {
                        future.get();
                    }
                    catch (CancellationException | ExecutionException ignore)
                    {
                        // Empty
                    }
                }
            }

            return futures;
        }
        catch (Throwable ex)
        {
            cancelAll(futures);

            throw ex;
        }
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException
    {
        Objects.requireNonNull(tasks, "tasks required");

        final long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;

        List<Future<T>> futures = new ArrayList<>(tasks.size());
        int j = 0;

        timedOut:
        try
        {
            for (Callable<T> task : tasks)
            {
                futures.add(newTaskFor(task));
            }

            final int size = futures.size();

            // Interleave time checks and calls to execute in case
            // executor doesn't have any/much parallelism.
            for (int i = 0; i < size; i++)
            {
                if (((i == 0) ? nanos : deadline - System.nanoTime()) <= 0L)
                {
                    break timedOut;
                }

                execute((Runnable) futures.get(i));
            }

            for (; j < size; j++)
            {
                Future<T> future = futures.get(j);

                if (!future.isDone())
                {
                    try
                    {
                        future.get(deadline - System.nanoTime(), NANOSECONDS);
                    }
                    catch (CancellationException | ExecutionException ignore)
                    {
                        // Empty
                    }
                    catch (TimeoutException timedOut)
                    {
                        break timedOut;
                    }
                }
            }

            return futures;
        }
        catch (Throwable ex)
        {
            cancelAll(futures);

            throw ex;
        }

        // Timed out before all the tasks could be completed; cancel remaining
        cancelAll(futures, j);

        return futures;
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
     */
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        throw new UnsupportedOperationException("Use Delegted ExecutorService instead");
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException
    {
        throw new UnsupportedOperationException("Use Delegted ExecutorService instead");
    }

    /**
     * @see java.util.concurrent.ExecutorService#isShutdown()
     */
    @Override
    public boolean isShutdown()
    {
        return getDelegate().isShutdown();
    }

    /**
     * @see java.util.concurrent.ExecutorService#isTerminated()
     */
    @Override
    public boolean isTerminated()
    {
        return getDelegate().isTerminated();
    }

    /**
     * Returns a {@code RunnableFuture} for the given callable task.
     *
     * @param callable the callable task being wrapped
     * @param <T> the type of the callable's result
     * @return a {@code RunnableFuture} which, when run, will call the underlying callable and which, as a {@code Future}, will yield the callable's result as
     *         its result and provide for cancellation of the underlying task
     * @since 1.6
     */
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable)
    {
        return new FutureTask<>(callable);
    }

    /**
     * Returns a {@code RunnableFuture} for the given runnable and default value.
     *
     * @param runnable the runnable task being wrapped
     * @param value the default value for the returned future
     * @param <T> the type of the given value
     * @return a {@code RunnableFuture} which, when run, will run the underlying runnable and which, as a {@code Future}, will yield the given value as its
     *         result and provide for cancellation of the underlying task
     * @since 1.6
     */
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value)
    {
        return new FutureTask<>(runnable, value);
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdown()
     */
    @Override
    public void shutdown()
    {
        throw new UnsupportedOperationException("Use Delegted ExecutorService instead");
    }

    /**
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    @Override
    public List<Runnable> shutdownNow()
    {
        throw new UnsupportedOperationException("Use Delegted ExecutorService instead");
    }

    /**
     * @see java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable)
     */
    @Override
    public <T> Future<T> submit(final Callable<T> task)
    {
        Objects.requireNonNull(task, "task required");

        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);

        return ftask;
    }

    /**
     * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable)
     */
    @Override
    public Future<?> submit(final Runnable task)
    {
        Objects.requireNonNull(task, "task required");

        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);

        return ftask;
    }

    /**
     * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable, java.lang.Object)
     */
    @Override
    public <T> Future<T> submit(final Runnable task, final T result)
    {
        Objects.requireNonNull(task, "task required");

        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);

        return ftask;
    }
}
