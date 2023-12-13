// Created: 12.02.2017
package de.freese.base.core.concurrent.pool;

import java.io.Serial;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * Das Default-Verhalten eines {@link ThreadPoolExecutor} mit einer Bounded-Queue ist, dass erst neue Threads erzeugt werden, wenn die corePoolSize erreicht und
 * die Queue voll ist.<br>
 * Bei folgender Konfiguration
 *
 * <pre>
 * corePoolSize = 3
 * maximumPoolSize = 10
 * queueCapacity = 20
 * </pre>
 *
 * würden die Threads 4 - 10 erst erzeugt werden, wenn in der Queue 20 Tasks liegen.<br>
 * Somit läuft der ThreadPool immer nur mit 3 Threads und nicht mit max. 10 wie erwartet, wenn z.B. 11 Tasks bearbeitet werden müssen.<br>
 * <br>
 * Die Lösung ist, die Methode {@link LinkedBlockingQueue#offer(Object)} so zu implementieren, dass FALSE geliefert wird, wenn die maximumPoolSize noch nicht
 * erreicht ist.<br>
 * Dies zwingt den {@link ThreadPoolExecutor} dazu neue Threads zu erzeugen, auch wenn die Queue noch nicht voll ist.<br>
 * <br>
 * Beispiel:
 *
 * <pre>
 * {@code
 * TunedLinkedBlockingQueue<Runnable> queue = new TunedLinkedBlockingQueue<>(SIZE);
 * ThreadPoolExecutor tpe = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
 * queue.setPoolCurrentSize(tpe::getPoolSize);
 * queue.setPoolMaxSize(tpe::getMaximumPoolSize);
 *  }</pre>
 *
 * @author Thomas Freese
 */
public class TunedLinkedBlockingQueue<T> extends LinkedBlockingQueue<T> {
    @Serial
    private static final long serialVersionUID = 6374300294609033461L;

    private transient IntSupplier poolCurrentSize;

    private transient IntSupplier poolMaxSize;

    public TunedLinkedBlockingQueue(final int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(final T e) {
        if (this.poolCurrentSize.getAsInt() < this.poolMaxSize.getAsInt()) {
            // FALSE triggert den ThreadPoolExecutor neue Threads zu erzeugen.
            return false;
        }

        return super.offer(e);
    }

    @Override
    public boolean offer(final T e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (this.poolCurrentSize.getAsInt() < this.poolMaxSize.getAsInt()) {
            // FALSE triggert den ThreadPoolExecutor neue Threads zu erzeugen.
            return false;
        }

        return super.offer(e, timeout, unit);
    }

    public void setPoolCurrentSize(final IntSupplier poolCurrentSize) {
        this.poolCurrentSize = poolCurrentSize;
    }

    public void setPoolMaxSize(final IntSupplier poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }
}
