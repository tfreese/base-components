// Created 10.09.2008
package de.freese.base.core.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basismplementierung eines Processors.
 *
 * @author Thomas Freese
 *
 * @param <C> Typ des Kontextobjekts
 */
public abstract class AbstractProcessor<C> implements Processor<C>
{
    /**
     *
     */
    private boolean enabled = true;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.base.core.processor.Processor#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * @see de.freese.base.core.processor.Processor#setEnabled(boolean)
     */
    @Override
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }
}
