// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class JulLoggerProvider implements LoggerProvider {
    @Override
    public Logger createLogger(String name) {
        return new JulLogger(name);
    }

    @Override
    public String toString() {
        return "Java Util Logging";
    }
}
