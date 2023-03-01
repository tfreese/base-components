package de.freese.base.swing.task;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
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

    private Date lastAccess = new Date();

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

    public Date getLastAccess() {
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
        long avg = getAvg();

        // Add new Times only when they >= 10% of the average.
        if (duration >= (avg * 0.1F)) {
            appendDuration(duration);
        }

        this.lastAccess = new Date();
    }

    public void setDurations(final long[] durations) {
        for (long zeit : durations) {
            appendDuration(zeit);
        }
    }

    public void setLastAccess(final Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setTaskName(final String taskName) {
        this.taskName = taskName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getTaskName());
        builder.append("; Min=").append(getMin());
        builder.append("; Max=").append(getMax());
        builder.append("; Avg=").append(getAvg());
        builder.append("; Size=").append(this.durations.size());

        String format = "%1$td.%1$tm.%1$tY %1$tT";
        builder.append("; Datum=").append(String.format(format, getLastAccess()));

        return builder.toString();
    }

    protected void appendDuration(final long zeit) {
        this.durations.add(zeit);

        if (this.durations.size() > MAX_SIZE) {
            this.durations.poll();
        }
    }
}
