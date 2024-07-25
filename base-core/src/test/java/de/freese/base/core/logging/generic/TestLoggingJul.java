// Created: 25 Juli 2024
package de.freese.base.core.logging.generic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

/**
 * @author Thomas Freese
 */
@Disabled("needs fork mode for tests")
class TestLoggingJul implements TestLogging {
    @BeforeAll
    static void beforeAll() {
        System.setProperty("logging.provider", "jul");
    }

    @Override
    public Class<? extends Logger> getLoggerClass() {
        return JulLogger.class;
    }
}
