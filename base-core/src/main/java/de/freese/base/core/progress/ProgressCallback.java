package de.freese.base.core.progress;

import java.util.function.BiConsumer;

/**
 * Interface für einen Progress-Verlauf.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ProgressCallback extends BiConsumer<Long, Long>
{
    ProgressCallback EMPTY = percentage ->
    {
    };

    @Override
    default void accept(Long value, Long max)
    {
        setProgress(value, max);
    }

    /**
     * @param percentage double 0-1
     */
    void setProgress(final double percentage);

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
