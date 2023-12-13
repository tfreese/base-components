// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class Slf4jLogger implements Logger {
    private final org.slf4j.Logger slf4jLogger;

    Slf4jLogger(final String name) {
        this.slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
    }

    @Override
    public void debug(final String message) {
        this.slf4jLogger.debug(message);
    }

    @Override
    public void error(final String message) {
        this.slf4jLogger.error(message);
    }

    @Override
    public void error(final String message, final Throwable error) {
        this.slf4jLogger.error(message, error);
    }

    @Override
    public void info(final String message) {
        this.slf4jLogger.info(message);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.slf4jLogger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.slf4jLogger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.slf4jLogger.isInfoEnabled();
    }
}
