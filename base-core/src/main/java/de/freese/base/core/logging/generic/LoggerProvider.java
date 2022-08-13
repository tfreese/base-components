// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
interface LoggerProvider
{
    default Logger createLogger(Class<?> clazz)
    {
        return createLogger(clazz.getName());
    }

    Logger createLogger(String name);
}
