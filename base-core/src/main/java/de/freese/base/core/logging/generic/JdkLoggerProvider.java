// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class JdkLoggerProvider implements LoggerProvider
{
    @Override
    public Logger createLogger(String name)
    {
        return new JdkLogger(name);
    }

    @Override
    public String toString()
    {
        return "Java Util Logging";
    }
}
