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
 * Geklaut von org.springframework.util.StopWatch.
 *
 * @author Thomas Freese
 */
public class StopWatch {
    private static final AtomicInteger ID_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger TASK_NUMBER = new AtomicInteger(1);

    /**
     * @author Thomas Freese
     */
    public static class DefaultPrettyPrinter implements Consumer<StopWatch> {
        private final PrintStream printStream;

        public DefaultPrettyPrinter(final PrintStream printStream) {
            super();

            this.printStream = Objects.requireNonNull(printStream, "printStream required");
        }

        @Override
        public void accept(final StopWatch sw) {
            printSummary(sw, printStream, TimeUnit.MILLISECONDS);
            printStream.println();
            printTasks(sw, printStream, TimeUnit.MILLISECONDS);

            printStream.flush();
        }

        protected void printSummary(final StopWatch sw, final PrintStream printStream, final TimeUnit timeUnit) {
            printStream.printf("StopWatch '%s': running time = %,d %s = %,d %s",
                    sw.getId(),
                    sw.getTotalTimeNanos(),
                    TimeUnit.NANOSECONDS.toChronoUnit(),
                    timeUnit.convert(sw.getTotalTimeNanos(), TimeUnit.NANOSECONDS),
                    timeUnit.toChronoUnit()
            )
            ;
        }

        protected void printTasks(final StopWatch sw, final PrintStream printStream, final TimeUnit timeUnit) {
            if (!sw.isKeepTaskList()) {
                printStream.println("No task info kept");
            }
            else {
                printStream.printf("%15s | %9s | %3s | Task Name%n", TimeUnit.NANOSECONDS.toChronoUnit(), timeUnit.toChronoUnit(), "%");

                for (TaskInfo task : sw.getTaskList()) {
                    final long nanos = task.getTime(TimeUnit.NANOSECONDS);

                    printStream.printf("%,15d | %,9d | %3.0f | %s%n",
                            nanos,
                            timeUnit.convert(nanos, TimeUnit.NANOSECONDS),
                            ((double) nanos / sw.getTotalTimeNanos()) * 100D,
                            task.taskName)
                    ;
                }
            }
        }
    }

    public record TaskInfo(String taskName, long timeNanos) {
        public TaskInfo {
            Objects.requireNonNull(taskName);
        }

        public long getTime(final TimeUnit timeUnit) {
            return timeUnit.convert(timeNanos, TimeUnit.NANOSECONDS);
        }
    }

    private final String id;
    private final List<TaskInfo> taskList = new LinkedList<>();

    private String currentTaskName;
    private boolean keepTaskList = true;
    private TaskInfo lastTaskInfo;
    private long startTimeNanos;
    private long totalTimeNanos;

    public StopWatch() {
        this("StopWatch-" + ID_NUMBER.getAndIncrement());
    }

    public StopWatch(final String id) {
        super();

        this.id = id;
    }

    public void clearTaskList() {
        taskList.clear();
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public String getId() {
        return id;
    }

    public TaskInfo getLastTaskInfo() {
        if (lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }

        return lastTaskInfo;
    }

    public int getTaskCount() {
        return taskList.size();
    }

    public List<TaskInfo> getTaskList() {
        final List<TaskInfo> copy = new ArrayList<>(getTaskCount());
        copy.addAll(taskList);

        return copy;
    }

    public long getTotalTimeNanos() {
        return totalTimeNanos;
    }

    /**
     * Configure whether the {@link TaskInfo} List is built over time.<br>
     * Set this to {@code false} when using a {@code StopWatch} for millions of intervals; otherwise, the {@code TaskInfo} structure will consume excessive
     * memory.<br>
     * Default is {@code true}.
     */
    public boolean isKeepTaskList() {
        return keepTaskList;
    }

    public boolean isRunning() {
        return getCurrentTaskName() != null;
    }

    /**
     * Prints all the task details on the {@link PrintStream}.
     */
    public void prettyPrint(final Consumer<StopWatch> consumer) {
        consumer.accept(this);
    }

    /**
     * Prints all the task details on the {@link PrintStream}.
     */
    public void prettyPrint(final PrintStream printStream) {
        prettyPrint(new DefaultPrettyPrinter(printStream));
    }

    /**
     * Configure whether the {@link TaskInfo} List is built over time.<br>
     * Set this to {@code false} when using a {@code StopWatch} for millions of intervals; otherwise, the {@code TaskInfo} structure will consume excessive
     * memory.<br>
     * Default is {@code true}.
     */
    public void setKeepTaskList(final boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }

    /**
     * Start an unnamed task.<br>
     * The results are undefined if {@link #stop()} is called without invoking this method first.
     */
    public void start() {
        start("Task-" + TASK_NUMBER.getAndIncrement());
    }

    /**
     * Start a named task.<br>
     * The results are undefined if {@link #stop()} is called without invoking this method first.t.
     */
    public void start(final String taskName) {
        if (currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }

        currentTaskName = taskName;
        startTimeNanos = System.nanoTime();
    }

    /**
     * Stop the current task.<br>
     * The results are undefined if {@code start()}is called.
     */
    public void stop() {
        if (currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }

        final long lastTime = System.nanoTime() - startTimeNanos;
        totalTimeNanos += lastTime;
        lastTaskInfo = new TaskInfo(currentTaskName, lastTime);

        if (keepTaskList) {
            taskList.add(lastTaskInfo);
        }

        currentTaskName = null;
    }
}
