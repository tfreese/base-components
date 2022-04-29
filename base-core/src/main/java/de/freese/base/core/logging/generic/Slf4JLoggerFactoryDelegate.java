// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class Slf4JLoggerFactoryDelegate implements LoggerFactoryDelegate
{
    @Override
    public Logger createLogger(Class<?> clazz)
    {
        return new Slf4jLogger(clazz);
    }

    @Override
    public String toString()
    {
        return "Slf4J";
    }
}
