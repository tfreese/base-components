// Created: 29.04.2022
package de.freese.base.core.logging.generic;

import java.util.logging.Level;

/**
 * @author Thomas Freese
 */
class JulLogger implements Logger
{
    /**
     *
     */
    private final java.util.logging.Logger julLogger;

    /**
     * @param clazz Class
     */
    public JulLogger(Class<?> clazz)
    {
        this.julLogger = java.util.logging.Logger.getLogger(clazz.getName());
    }

    @Override
    public void debug(String message)
    {
        this.julLogger.log(Level.FINE, message);
    }

    @Override
    public void error(String message)
    {
        this.julLogger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable error)
    {
        this.julLogger.log(Level.SEVERE, message, error);
    }

    @Override
    public void info(String message)
    {
        this.julLogger.log(Level.INFO, message);
    }

    @Override
    public boolean isDebugEnabled()
    {
        return this.julLogger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isErrorEnabled()
    {
        return this.julLogger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isInfoEnabled()
    {
        return this.julLogger.isLoggable(Level.INFO);
    }
}
