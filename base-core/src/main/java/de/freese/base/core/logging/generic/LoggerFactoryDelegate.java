// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
interface LoggerFactoryDelegate
{
    /**
     * @param clazz Class
     *
     * @return Logger
     */
    Logger createLogger(Class<?> clazz);
}
