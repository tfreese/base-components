// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
class Slf4JLoggerProvider implements LoggerProvider {
    @Override
    public Logger createLogger(final String name) {
        return new Slf4jLogger(name);
    }

    @Override
    public String toString() {
        return "Slf4J";
    }
}
