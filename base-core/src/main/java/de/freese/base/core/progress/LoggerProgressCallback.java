package de.freese.base.core.progress;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementierung des {@link ProgressCallback} Interfaces für den {@link Logger}.
 *
 * @author Thomas Freese
 */
public class LoggerProgressCallback implements ProgressCallback
{
    /**
     *
     */
    private final Logger logger;

    /**
     * Erstellt ein neues {@link LoggerProgressCallback} Objekt.
     */
    public LoggerProgressCallback()
    {
        this(LoggerFactory.getLogger(LoggerProgressCallback.class));
    }

    /**
     * Erstellt ein neues {@link LoggerProgressCallback} Objekt.
     *
     * @param logger {@link Logger}
     */
    public LoggerProgressCallback(final Logger logger)
    {
        super();

        this.logger = Objects.requireNonNull(logger, "logger required");
    }

    /**
     * @see de.freese.base.core.progress.ProgressCallback#setProgress(float)
     */
    @Override
    public void setProgress(final float percentage)
    {
        if (this.logger.isInfoEnabled())
        {
            this.logger.info(String.format("%1$3.2f %%", percentage * 100));
        }
    }
}
