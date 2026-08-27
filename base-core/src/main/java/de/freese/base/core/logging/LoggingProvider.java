package de.freese.base.core.logging;

import org.slf4j.Logger;

/**
 * @author Thomas Freese
 * @since 26.02.2020
 */
@FunctionalInterface
public interface LoggingProvider {
    Logger getLogger();
}
