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
public class SyncFuture<T> implements Future<T> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final long startTime = System.currentTimeMillis();

    private T result;

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        latch.await();

        return result;
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return result;
        }

        return null;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    public void setResult(final T result) {
        this.result = result;

        latch.countDown();
    }
}
