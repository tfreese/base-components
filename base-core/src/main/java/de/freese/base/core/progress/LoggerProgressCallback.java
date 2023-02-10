package de.freese.base.core.progress;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementierung des {@link ProgressCallback} Interfaces für den {@link Logger}.
 *
 * @author Thomas Freese
 */
public class LoggerProgressCallback implements ProgressCallback {
    private final Logger logger;

    public LoggerProgressCallback() {
        this(LoggerFactory.getLogger(LoggerProgressCallback.class));
    }

    public LoggerProgressCallback(final Logger logger) {
        super();

        this.logger = Objects.requireNonNull(logger, "logger required");
    }

    /**
     * @see de.freese.base.core.progress.ProgressCallback#setProgress(double)
     */
    @Override
    public void setProgress(final double percentage) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(String.format("%1$3.2f %%", percentage * 100D));
        }
    }
}
