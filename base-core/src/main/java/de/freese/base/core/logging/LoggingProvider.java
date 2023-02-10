// Created: 26.02.2020
package de.freese.base.core.logging;

import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface LoggingProvider {
    Logger getLogger();
}
