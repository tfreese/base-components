package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 * @since 29.04.2022
 */
class JulLoggerProvider implements LoggerProvider {
    @Override
    public Logger createLogger(final String name) {
        return new JulLogger(name);
    }

    @Override
    public String toString() {
        return "Java Util Logging";
    }
}
