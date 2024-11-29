// Created: 29.04.2022
package de.freese.base.core.logging.generic;

import java.util.logging.Level;

/**
 * @author Thomas Freese
 */
class JulLogger implements Logger {
    private final java.util.logging.Logger logger;

    JulLogger(final String name) {
        super();

        this.logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public void debug(final String message) {
        logger.log(Level.FINE, message);
    }

    @Override
    public void error(final String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(final String message, final Throwable error) {
        logger.log(Level.SEVERE, message, error);
    }

    @Override
    public void info(final String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }
}
