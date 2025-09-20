// Created: 09.04.2020
package de.freese.base.utils;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.concurrent.NamedThreadFactory;

/**
 * @author Thomas Freese
 */
public final class ExecutorUtils {
    /**
     * <pre>
     * Defaults:
     * - coreSize = {@link Runtime#availableProcessors()}
     * - maxSize = coreSize * 2
     * - queueSize = maxSize * 10
     * - keepAliveTime = 60
     * - rejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy
     * - allowCoreThreadTimeOut = false
     * - exposeUnconfigurableExecutor = true
     * </pre>
     *
     * @param threadNamePattern String; Beispiel: thread-%02d<br>
     */
    public static ExecutorService createThreadPool(final String threadNamePattern) {
        final int coreSize = Math.max(2, Runtime.getRuntime().availableProcessors());
        final int maxSize = coreSize * 2;
        final int queueSize = maxSize * 10;
        final int keepAliveSeconds = 60;

        return createThreadPool(threadNamePattern, coreSize, maxSize, queueSize, keepAliveSeconds);
    }

    /**
     * <pre>
     * Defaults:
     * - rejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy
     * - allowCoreThreadTimeOut = false
     * - exposeUnconfigurableExecutor = true
     * </pre>
     *
     * @param threadNamePattern String; Beispiel: thread-%02d<br>
     * @param queueSize int Set the capacity for the ThreadPoolExecutor's BlockingQueue. Any positive value will lead to a LinkedBlockingQueue instance; any
     * other value will lead to a SynchronousQueue instance.
     */
    public static ExecutorService createThreadPool(final String threadNamePattern, final int coreSize, final int maxSize, final int queueSize, final int keepAliveSeconds) {
        return createThreadPool(threadNamePattern, coreSize, maxSize, queueSize, keepAliveSeconds, new ThreadPoolExecutor.AbortPolicy(), false, true);
    }

    /**
     * @param threadNamePattern String; Beispiel: thread-%02d
     * @param queueSize int Set the capacity for the ThreadPoolExecutor's BlockingQueue. Any positive value will lead to a LinkedBlockingQueue instance; any
     * other value will lead to a SynchronousQueue instance.
     * @param allowCoreThreadTimeOut boolean If false (default), core threads stay alive even when idle. If true, core threads use keepAliveTime to time out
     * waiting for work.
     * @param exposeUnconfigurableExecutor boolean Should expose an unconfigurable decorator for the created executor.
     */
    public static ExecutorService createThreadPool(final String threadNamePattern, final int coreSize, final int maxSize, final int queueSize, final int keepAliveSeconds,
                                                   final RejectedExecutionHandler rejectedExecutionHandler, final boolean allowCoreThreadTimeOut,
                                                   final boolean exposeUnconfigurableExecutor) {
        final BlockingQueue<Runnable> queue;

        if (queueSize > 0) {
            queue = new LinkedBlockingQueue<>(queueSize);
        }
        else {
            queue = new SynchronousQueue<>();
        }

        final ThreadFactory threadFactory = new NamedThreadFactory(threadNamePattern, true);

        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);

        threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);

        return exposeUnconfigurableExecutor ? Executors.unconfigurableExecutorService(threadPoolExecutor) : threadPoolExecutor;
    }

    public static void shutdown(final AsynchronousChannelGroup channelGroup, final Logger logger) {
        logger.info("shutdown AsynchronousChannelGroup");

        if (channelGroup == null) {
            return;
        }

        channelGroup.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!channelGroup.awaitTermination(10, TimeUnit.SECONDS)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Timed out while waiting for channelGroup");
                }

                channelGroup.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!channelGroup.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("ChannelGroup did not terminate");
                }
            }
        }
        catch (InterruptedException | IOException _) {
            if (logger.isWarnEnabled()) {
                logger.warn("Interrupted while waiting for ChannelGroup");
            }

            // (Re-)Cancel if current thread also interrupted
            try {
                channelGroup.shutdownNow();
            }
            catch (IOException _) {
                logger.error("ChannelGroup did not terminate");
            }

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static void shutdown(final ExecutorService executorService) {
        shutdown(executorService, LoggerFactory.getLogger(ExecutorUtils.class));
    }

    public static void shutdown(final ExecutorService executorService, final Logger logger) {
        logger.info("shutdown ExecutorService");

        if (executorService == null) {
            logger.warn("ExecutorService is null");

            return;
        }

        executorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Timed out while waiting for ExecutorService");

                // Cancel currently executing tasks.
                executorService.shutdownNow().stream()
                        .filter(Future.class::isInstance)
                        .map(Future.class::cast)
                        .forEach(future -> future.cancel(true))
                ;

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("ExecutorService did not terminate");
                }
                else {
                    logger.info("ExecutorService terminated");
                }
            }
            else {
                logger.info("ExecutorService terminated");
            }
        }
        catch (InterruptedException _) {
            logger.warn("Interrupted while waiting for ExecutorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    private ExecutorUtils() {
        super();
    }
}
