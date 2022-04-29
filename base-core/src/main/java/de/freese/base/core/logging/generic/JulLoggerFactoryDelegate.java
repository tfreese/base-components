// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class JulLoggerFactoryDelegate implements LoggerFactoryDelegate
{
    @Override
    public Logger createLogger(Class<?> clazz)
    {
        return new JulLogger(clazz);
    }

    @Override
    public String toString()
    {
        return "Java Util Logging";
    }
}
