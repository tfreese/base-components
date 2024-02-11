package de.freese.base.swing.task;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Thomas Freese
 */
public class TaskStatistik implements Serializable {
    private static final int MAX_SIZE = 10;

    @Serial
    private static final long serialVersionUID = 1789042123984173851L;
    /**
     * Keep n Values.
     */
    private final transient Queue<Long> durations = new ConcurrentLinkedQueue<>();

    private LocalDateTime lastAccess = LocalDateTime.now();
    private String taskName = "";

    public long getAvg() {
        int anzahl = 0;
        long summe = 0;

        for (long zeit : this.durations) {
            summe += zeit;
            anzahl++;
        }

        return summe > 0 ? summe / anzahl : 0;
    }

    public LocalDateTime getLastAccess() {
        return this.lastAccess;
    }

    public long getMax() {
        long max = Long.MIN_VALUE;

        for (long zeit : this.durations) {
            max = Math.max(max, zeit);
        }

        return max == Long.MIN_VALUE ? 0 : max;
    }

    public long getMin() {
        long min = Long.MAX_VALUE;

        for (long zeit : this.durations) {
            min = Math.min(min, zeit);
        }

        return min == Long.MAX_VALUE ? 0 : min;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void measureDuration(final long duration) {
        final long avg = getAvg();

        // Add new Times only when they >= 10% of the average.
        if (duration >= (avg * 0.1D)) {
            appendDuration(duration);
        }

        this.lastAccess = LocalDateTime.now();
    }

    public void setDurations(final long[] durations) {
        for (long zeit : durations) {
            appendDuration(zeit);
        }
    }

    public void setLastAccess(final LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(getTaskName());
        builder.append("; Min=").append(getMin());
        builder.append("; Max=").append(getMax());
        builder.append("; Avg=").append(getAvg());
        builder.append("; Size=").append(this.durations.size());
        builder.append("; Datum=").append(getLastAccess());

        return builder.toString();
    }

    protected void appendDuration(final long zeit) {
        this.durations.add(zeit);

        if (this.durations.size() > MAX_SIZE) {
            this.durations.poll();
        }
    }
}
