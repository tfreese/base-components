// Created: 26.02.2020
package de.freese.base.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLoggingProvider implements LoggingProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractLoggingProvider() {
        super();
    }

    /**
     * @see de.freese.base.core.logging.LoggingProvider#getLogger()
     */
    @Override
    public Logger getLogger() {
        return this.logger;
    }
}
