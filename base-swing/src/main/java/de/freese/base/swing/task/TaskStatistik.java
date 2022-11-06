package de.freese.base.swing.task;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Enthält Informationen über die Ausführungszeiten von {@link AbstractSwingTask}s.
 *
 * @author Thomas Freese
 */
public class TaskStatistik implements Serializable
{
    private static final int MAX_SIZE = 10;

    @Serial
    private static final long serialVersionUID = 1789042123984173851L;
    /**
     * Immer N Werte vorhalten.
     */
    private final transient Queue<Long> durations = new ConcurrentLinkedQueue<>();

    private Date lastAccess = new Date();

    private String taskName = "";

    /**
     * Durchschnittliche Ausführungszeit in ms.
     */
    public long getAvg()
    {
        int anzahl = 0;
        long summe = 0;

        for (long zeit : this.durations)
        {
            summe += zeit;
            anzahl++;
        }

        return summe > 0 ? summe / anzahl : 0;
    }

    public Date getLastAccess()
    {
        return this.lastAccess;
    }

    // /**
    // * Liefert alle gemessenen Zeiten (ms).
    // */
    // public long[] getDurations()
    // {
    // long[] values = new long[this.durations.size()];
    //
    // // for (int i = 0; i < values.length; i++)
    // // {
    // // values[i] = this.durations.get(i);
    // // }
    // int i = 0;
    //
    // for (Iterator<Long> iterator = this.durations.iterator(); iterator.hasNext();)
    // {
    // values[i++] = iterator.next();
    //
    // }
    //
    // return values;
    // }

    /**
     * Maximale Ausführungszeit in ms.
     */
    public long getMax()
    {
        long max = Long.MIN_VALUE;

        for (long zeit : this.durations)
        {
            max = Math.max(max, zeit);
        }

        return max == Long.MIN_VALUE ? 0 : max;
    }

    /**
     * Minimale Ausführungszeit in ms.
     */
    public long getMin()
    {
        long min = Long.MAX_VALUE;

        for (long zeit : this.durations)
        {
            min = Math.min(min, zeit);
        }

        return min == Long.MAX_VALUE ? 0 : min;
    }

    public String getTaskName()
    {
        return this.taskName;
    }

    /**
     * Setzt neue gemessene Ausführungszeit in ms.
     */
    public void measureDuration(final long duration)
    {
        long avg = getAvg();

        // Neue Zeiten nur berücksichtigen, wenn sie >= 10% des Durchschnitts sind
        if (duration >= (avg * 0.1F))
        {
            appendDuration(duration);
        }

        this.lastAccess = new Date();
    }

    /**
     * Setzt alle gemessenen Zeiten (ms).
     */
    public void setDurations(final long[] durations)
    {
        for (long zeit : durations)
        {
            appendDuration(zeit);
        }
    }

    public void setLastAccess(final Date lastAccess)
    {
        this.lastAccess = lastAccess;
    }

    public void setTaskName(final String taskName)
    {
        this.taskName = taskName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(getTaskName());
        builder.append("; Min=" + getMin());
        builder.append("; Max=" + getMax());
        builder.append("; Avg=" + getAvg());
        builder.append("; Size=" + this.durations.size());

        String format = "%1$td.%1$tm.%1$tY %1$tT";
        builder.append("; Datum=").append(String.format(format, getLastAccess()));

        return builder.toString();
    }

    /**
     * Hinzufügen einer Zeit und ggf. entfernen der ältesten Zeit aus der Liste.
     */
    protected void appendDuration(final long zeit)
    {
        this.durations.add(zeit);

        if (this.durations.size() > MAX_SIZE)
        {
            this.durations.poll();
        }
    }
}
