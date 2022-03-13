// Created: 15.04.2020
package de.freese.base.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Geklaut und elegantisiert von org.springframework.util.StopWatch.
 *
 * @author Thomas Freese
 */
public class StopWatch
{
    /**
     *
     */
    private static final AtomicInteger ID_NUMBER = new AtomicInteger(1);
    /**
     *
     */
    private static final AtomicInteger TASK_NUMBER = new AtomicInteger(1);

    /**
     * @author Thomas Freese
     */
    public static class DefaultPrettyPrinter implements Consumer<StopWatch>
    {
        /**
         *
         */
        private final PrintStream printStream;

        /**
         * Erstellt ein neues {@link DefaultPrettyPrinter} Object.
         *
         * @param printStream {@link PrintStream}
         */
        public DefaultPrettyPrinter(final PrintStream printStream)
        {
            super();

            this.printStream = Objects.requireNonNull(printStream, "printStream required");
        }

        /**
         * @see java.util.function.Consumer#accept(java.lang.Object)
         */
        @Override
        public void accept(final StopWatch sw)
        {
            printSummary(sw, this.printStream, TimeUnit.MILLISECONDS);
            this.printStream.println();
            printTasks(sw, this.printStream, TimeUnit.MILLISECONDS);

            this.printStream.flush();
        }

        /**
         * @param sw {@link StopWatch}
         * @param printStream {@link PrintStream}
         * @param timeUnit {@link TimeUnit}
         */
        protected void printSummary(final StopWatch sw, final PrintStream printStream, final TimeUnit timeUnit)
        {
            // @formatter:off
            printStream.printf("StopWatch '%s': running time = %,d %s = %,d %s"
                    , sw.getId()
                    , sw.getTotalTimeNanos()
                    , TimeUnit.NANOSECONDS.toChronoUnit()
                    , timeUnit.convert(sw.getTotalTimeNanos(), TimeUnit.NANOSECONDS)
                    , timeUnit.toChronoUnit()
                    )
                    ;
            // @formatter:on
        }

        /**
         * @param sw {@link StopWatch}
         * @param printStream {@link PrintStream}
         * @param timeUnit {@link TimeUnit}
         */
        protected void printTasks(final StopWatch sw, final PrintStream printStream, final TimeUnit timeUnit)
        {
            if (!sw.isKeepTaskList())
            {
                printStream.println("No task info kept");
            }
            else
            {
                printStream.printf("%15s | %9s | %3s | Task Name%n", TimeUnit.NANOSECONDS.toChronoUnit(), timeUnit.toChronoUnit(), "%");

                for (TaskInfo task : sw.getTaskList())
                {
                    long nanos = task.getTime(TimeUnit.NANOSECONDS);

                    // @formatter:off
                    printStream.printf("%,15d | %,9d | %3.0f | %s%n"
                            , nanos
                            , timeUnit.convert(nanos, TimeUnit.NANOSECONDS)
                            , ((double) nanos / sw.getTotalTimeNanos()) * 100D
                            , task.taskName)
                            ;
                    // @formatter:off
                }
            }
        }
    }

    /**
     * @param taskName String
     * @param timeNanos long
     *
     * @author Thomas Freese
     */
    public record TaskInfo(String taskName, long timeNanos)
    {
        /**
         * Erstellt ein neues {@link TaskInfo} Object.
         *
         * @param taskName String
         * @param timeNanos long
         */
        public TaskInfo
        {
            Objects.requireNonNull(taskName);
        }

        /**
         * @param timeUnit {@link TimeUnit}
         *
         * @return long
         */
        public long getTime(final TimeUnit timeUnit)
        {
            return timeUnit.convert(this.timeNanos, TimeUnit.NANOSECONDS);
        }
    }

    /**
     *
     */
    private final String id;
    /**
     *
     */
    private final List<TaskInfo> taskList = new LinkedList<>();
    /**
     *
     */
    private String currentTaskName;
    /**
     *
     */
    private boolean keepTaskList = true;
    /**
     *
     */
    private TaskInfo lastTaskInfo;
    /**
     * Start time of the current task.
     */
    private long startTimeNanos;
    /**
     * Total running time.
     */
    private long totalTimeNanos;

    /**
     * Erstellt ein neues {@link StopWatch} Object.
     */
    public StopWatch()
    {
        this("StopWatch-" + ID_NUMBER.getAndIncrement());
    }

    /**
     * Erstellt ein neues {@link StopWatch} Object.
     *
     * @param id String
     */
    public StopWatch(final String id)
    {
        super();

        this.id = id;
    }

    /**
     *
     */
    public void clearTaskList()
    {
        this.taskList.clear();
    }

    /**
     * Get the name of the currently running task, if any.
     *
     * @return String
     */
    public String getCurrentTaskName()
    {
        return this.currentTaskName;
    }

    /**
     * @return String
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return {@link TaskInfo}
     */
    public TaskInfo getLastTaskInfo()
    {
        if (this.lastTaskInfo == null)
        {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }

        return this.lastTaskInfo;
    }

    /**
     * Get the number of tasks timed.
     *
     * @return int
     */
    public int getTaskCount()
    {
        return this.taskList.size();
    }

    /**
     * Returns a copy of the TaskList.
     *
     * @return List<TaskInfo>
     */
    public List<TaskInfo> getTaskList()
    {
        List<TaskInfo> copy = new ArrayList<>(getTaskCount());
        copy.addAll(this.taskList);

        return copy;
    }

    /**
     * Get the total time in nanoseconds for all tasks.
     *
     * @return long
     */
    public long getTotalTimeNanos()
    {
        return this.totalTimeNanos;
    }

    /**
     * Configure whether the {@link TaskInfo} List is built over time.<br>
     * Set this to {@code false} when using a {@code StopWatch} for millions of intervals; otherwise, the {@code TaskInfo} structure will consume excessive
     * memory.<br>
     * Default is {@code true}.
     *
     * @return boolean
     */
    public boolean isKeepTaskList()
    {
        return this.keepTaskList;
    }

    /**
     * Determine whether this {@code StopWatch} is currently running.
     *
     * @return boolean
     */
    public boolean isRunning()
    {
        return getCurrentTaskName() != null;
    }

    /**
     * Prints all the task details on the {@link PrintStream}.
     *
     * @param consumer {@link Consumer}
     *
     * @see DefaultPrettyPrinter
     */
    public void prettyPrint(final Consumer<StopWatch> consumer)
    {
        consumer.accept(this);
    }

    /**
     * Prints all the task details on the {@link PrintStream}.
     *
     * @param printStream {@link PrintStream}
     *
     * @see DefaultPrettyPrinter
     */
    public void prettyPrint(final PrintStream printStream)
    {
        prettyPrint(new DefaultPrettyPrinter(printStream));
    }

    /**
     * Configure whether the {@link TaskInfo} List is built over time.<br>
     * Set this to {@code false} when using a {@code StopWatch} for millions of intervals; otherwise, the {@code TaskInfo} structure will consume excessive
     * memory.<br>
     * Default is {@code true}.
     *
     * @param keepTaskList boolean
     */
    public void setKeepTaskList(final boolean keepTaskList)
    {
        this.keepTaskList = keepTaskList;
    }

    /**
     * Start an unnamed task.<br>
     * The results are undefined if {@link #stop()} is called without invoking this method first.
     */
    public void start()
    {
        start("Task-" + TASK_NUMBER.getAndIncrement());
    }

    /**
     * Start a named task.<br>
     * The results are undefined if {@link #stop()} is called without invoking this method first.t.
     *
     * @param taskName String
     */
    public void start(final String taskName)
    {
        if (this.currentTaskName != null)
        {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }

        this.currentTaskName = taskName;
        this.startTimeNanos = System.nanoTime();
    }

    /**
     * Stop the current task.<br>
     * The results are undefined if {@code start()}is called.
     *
     * @see #start()
     * @see #start(String)
     */
    public void stop()
    {
        if (this.currentTaskName == null)
        {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }

        long lastTime = System.nanoTime() - this.startTimeNanos;
        this.totalTimeNanos += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);

        if (this.keepTaskList)
        {
            this.taskList.add(this.lastTaskInfo);
        }

        this.currentTaskName = null;
    }
}
