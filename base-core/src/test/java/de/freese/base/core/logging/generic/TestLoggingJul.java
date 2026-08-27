package de.freese.base.core.logging.generic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

/**
 * @author Thomas Freese
 * @since 25.07.2024
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
