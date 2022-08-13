// Created: 12.02.2017
package de.freese.base.core.concurrent.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

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
 *
 * @author Thomas Freese
 */
public class TunedThreadPoolExecutorFactoryBean extends ThreadPoolExecutorFactoryBean
{
    /**
     *
     */
    private static final long serialVersionUID = 4992566896817015389L;

    /**
     * @see org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean#createExecutor(int, int, int, java.util.concurrent.BlockingQueue,
     * java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)
     */
    @Override
    protected ThreadPoolExecutor createExecutor(final int corePoolSize, final int maxPoolSize, final int keepAliveSeconds, final BlockingQueue<Runnable> queue,
                                                final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler)
    {
        ThreadPoolExecutor tpe =
                new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);

        if (queue instanceof TunedLinkedBlockingQueue tlbq)
        {
            tlbq.setPoolCurrentSize(tpe::getPoolSize);
            tlbq.setPoolMaxSize(tpe::getMaximumPoolSize);
        }

        return tpe;
    }

    /**
     * @see org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean#createQueue(int)
     */
    @Override
    protected BlockingQueue<Runnable> createQueue(final int queueCapacity)
    {
        if (queueCapacity > 0)
        {
            return new TunedLinkedBlockingQueue<>(queueCapacity);
        }

        return new SynchronousQueue<>();
    }
}
