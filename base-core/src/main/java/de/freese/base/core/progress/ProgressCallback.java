package de.freese.base.core.progress;

import java.util.function.BiConsumer;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ProgressCallback extends BiConsumer<Integer, Integer> {
    ProgressCallback EMPTY = percentage -> {
    };

    @Override
    default void accept(final Integer value, final Integer max) {
        setProgress(value, max);
    }

    /**
     * @param percentage double 0-1
     */
    void setProgress(double percentage);

    default void setProgress(final int value, final int max) {
        setProgress(value, (long) max);
    }

    default void setProgress(final long value, final long max) {
        if (value <= 0L || value > max) {
            throw new IllegalArgumentException("invalid value: " + value);
        }

        final double percentage = (double) value / (double) max;

        setProgress(percentage);
    }
}
