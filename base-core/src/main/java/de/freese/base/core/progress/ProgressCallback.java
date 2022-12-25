package de.freese.base.core.progress;

import java.util.function.BiConsumer;

/**
 * Interface für einen Progress-Verlauf.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ProgressCallback extends BiConsumer<Integer, Integer>
{
    ProgressCallback EMPTY = percentage ->
    {
    };

    @Override
    default void accept(Integer value, Integer max)
    {
        setProgress(value, max);
    }

    /**
     * @param percentage double 0-1
     */
    void setProgress(double percentage);

    default void setProgress(final int value, final int max)
    {
        setProgress(value, (long) max);
    }

    default void setProgress(final long value, final long max)
    {
        if ((value <= 0) || (value > max))
        {
            throw new IllegalArgumentException("invalid value: " + value);
        }

        double percentage = (double) value / (double) max;

        setProgress(percentage);
    }
}
