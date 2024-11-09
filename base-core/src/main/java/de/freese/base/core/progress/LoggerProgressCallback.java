package de.freese.base.core.progress;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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

    @Override
    public void setProgress(final double percentage) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("{}", "%1$3.2f %%".formatted(percentage * 100D));
        }
    }
}
