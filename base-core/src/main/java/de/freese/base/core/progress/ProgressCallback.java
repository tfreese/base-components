package de.freese.base.core.progress;

/**
 * Interface für einen Progress-Verlauf.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ProgressCallback
{
    /**
     * @param percentage float 0-1
     */
    void setProgress(final float percentage);

    default void setProgress(final long value, final long max)
    {
        if ((value <= 0) || (value > max))
        {
            throw new IllegalArgumentException("invalid value: " + value);
        }

        float percentage = (float) value / (float) max;

        setProgress(percentage);
    }
}
