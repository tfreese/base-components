// Created: 25 Juli 2024
package de.freese.base.core.logging.generic;

import org.junit.jupiter.api.BeforeAll;

/**
 * @author Thomas Freese
 */
class TestLoggingSlf4J implements TestLogging {
    @BeforeAll
    static void beforeAll() {
        System.setProperty("logging.provider", "slf4j");
    }

    @Override
    public Class<? extends Logger> getLoggerClass() {
        return Slf4jLogger.class;
    }
}
