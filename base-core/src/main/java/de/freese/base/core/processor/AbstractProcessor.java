// Created 10.09.2008
package de.freese.base.core.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines Processors.
 *
 * @param <C> Typ des Kontextobjekts
 *
 * @author Thomas Freese
 */
public abstract class AbstractProcessor<C> implements Processor<C>
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean enabled = true;

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

    protected final Logger getLogger()
    {
        return this.logger;
    }
}
