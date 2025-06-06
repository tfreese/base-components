// Created 10.09.2008
package de.freese.base.core.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractProcessor<C> implements Processor<C> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean enabled = true;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    protected final Logger getLogger() {
        return logger;
    }
}
