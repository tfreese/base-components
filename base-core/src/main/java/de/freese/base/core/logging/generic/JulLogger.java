// Created: 29.04.2022
package de.freese.base.core.logging.generic;

import java.util.logging.Level;

/**
 * @author Thomas Freese
 */
class JulLogger implements Logger {
    private final java.util.logging.Logger logger;

    JulLogger(final String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public void debug(final String message) {
        this.logger.log(Level.FINE, message);
    }

    @Override
    public void error(final String message) {
        this.logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(final String message, final Throwable error) {
        this.logger.log(Level.SEVERE, message, error);
    }

    @Override
    public void info(final String message) {
        this.logger.log(Level.INFO, message);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }
}
