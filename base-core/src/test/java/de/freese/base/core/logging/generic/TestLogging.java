// Created: 25 Juli 2024
package de.freese.base.core.logging.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
public interface TestLogging {
    @BeforeEach
    default void beforeEach() {
        System.out.printf("%s, !!! Runs only in fork mode !!! ProcessHandle: %d%n", getClass().getSimpleName(), ProcessHandle.current().pid());
    }

    default void doLog(final Logger logger) {
        logger.info("info");
        logger.debug("debug");
        logger.error("debug");
        logger.error("error", new Exception("Test"));

        logger.info("%s - %d", "arg", 1);
        logger.debug("%s - %d", "arg", 1);
        logger.error("%s - %d", new Exception("Test"), "arg", 1);
    }

    Class<? extends Logger> getLoggerClass();

    @Test
    default void testLogging() {
        final Logger logger = LoggerFactory.createLogger(getClass());

        assertEquals(getLoggerClass(), logger.getClass());

        // doLog(logger);
    }
}
