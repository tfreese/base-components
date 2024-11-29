// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class Slf4jLogger implements Logger {
    private final org.slf4j.Logger logger;

    Slf4jLogger(final String name) {
        super();

        this.logger = org.slf4j.LoggerFactory.getLogger(name);
    }

    @Override
    public void debug(final String message) {
        logger.debug(message);
    }

    @Override
    public void error(final String message) {
        logger.error(message);
    }

    @Override
    public void error(final String message, final Throwable error) {
        logger.error(message, error);
    }

    @Override
    public void info(final String message) {
        logger.info(message);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }
}
