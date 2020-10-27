package de.freese.base.core.concurrent.pool;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class CustomizableThreadFactory implements ThreadFactory
{
    /**
     * @author Thomas Freese
     */
    public static class Builder implements de.freese.base.core.model.builder.Builder<ThreadFactory>
    {
        /**
        *
        */
        private boolean daemon = true;

        /**
         *
         */
        private String threadNamePattern = "thread-%02d";

        /**
         *
         */
        private int threadPriority = Thread.NORM_PRIORITY;

        /**
         *
         */
        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        /**
         *
         */
        private ThreadFactory wrappedFactory = Executors.defaultThreadFactory();

        /**
         * @see de.freese.base.core.model.builder.Builder#build()
         */
        @Override
        public ThreadFactory build()
        {
            ThreadFactory threadFactory = new CustomizableThreadFactory(this);

            return threadFactory;
        }

        /**
         * Default: true
         *
         * @param daemon boolean
         * @return {@link Builder}
         */
        public Builder daemon(final boolean daemon)
        {
            this.daemon = daemon;

            return this;
        }

        /**
         * Default: thread-%02d
         *
         * @param threadNamePattern String
         * @return {@link Builder}
         */
        public Builder threadNamePattern(final String threadNamePattern)
        {
            if ((threadNamePattern == null) || threadNamePattern.strip().isEmpty())
            {
                throw new IllegalArgumentException("threadNamePattern required");
            }

            this.threadNamePattern = threadNamePattern.strip();

            return this;
        }

        /**
         * Default: Thread.NORM_PRIORITY
         *
         * @param priority int
         * @return {@link Builder}
         */
        public Builder threadPriority(final int priority)
        {
            if ((priority < Thread.MIN_PRIORITY) || (priority > Thread.MAX_PRIORITY))
            {
                throw new IllegalArgumentException("priority must be >= Thread.MIN_PRIORITY and <= Thread.MAX_PRIORITY");
            }

            this.threadPriority = priority;

            return this;
        }

        /**
         * Default: thread-%02d
         *
         * @param uncaughtExceptionHandler {@link UncaughtExceptionHandler}
         * @return {@link Builder}
         */
        public Builder uncaughtExceptionHandler(final Thread.UncaughtExceptionHandler uncaughtExceptionHandler)
        {
            this.uncaughtExceptionHandler = Objects.requireNonNull(uncaughtExceptionHandler, "uncaughtExceptionHandler required");

            return this;
        }

        /**
         * Default: {@link Executors#defaultThreadFactory()}
         *
         * @param wrappedFactory {@link ThreadFactory}
         * @return {@link Builder}
         */
        public Builder wrappedFactory(final ThreadFactory wrappedFactory)
        {
            this.wrappedFactory = Objects.requireNonNull(wrappedFactory, "wrappedFactory required");

            return this;
        }
    }

    /**
    *
    */
    private final boolean daemon;

    /**
     *
     */
    private final String threadNamePattern;

    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     *
     */
    private final int threadPriority;

    /**
     *
     */
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
    *
    */
    private final ThreadFactory wrappedFactory;

    /**
     * Erzeugt eine neue Instanz von {@link CustomizableThreadFactory}
     *
     * @param builder {@link Builder}
     */
    private CustomizableThreadFactory(final Builder builder)
    {
        super();

        this.threadNamePattern = builder.threadNamePattern;
        this.threadPriority = builder.threadPriority;
        this.wrappedFactory = builder.wrappedFactory;
        this.uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
        this.daemon = builder.daemon;
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable task)
    {
        final String threadName = String.format(this.threadNamePattern, this.threadNumber.getAndIncrement());

        final Thread thread = this.wrappedFactory.newThread(task);

        thread.setName(threadName);

        // if (thread.isDaemon())
        // {
        thread.setDaemon(this.daemon);
        // }

        if (thread.getPriority() != this.threadPriority)
        {
            thread.setPriority(this.threadPriority);
        }

        if (this.uncaughtExceptionHandler != null)
        {
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        }

        return thread;
    }
}