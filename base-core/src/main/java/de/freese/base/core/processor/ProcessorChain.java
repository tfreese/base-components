/**
 * Created 10.07.2008
 */
package de.freese.base.core.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Enthält eine Liste von {@link Processor} des Interceptor/Filter Patterns, die nacheinander abgearbeitet werden.
 *
 * @author Thomas Freese
 * @param <C> Typ des Kontextobjekts
 */
public final class ProcessorChain<C> implements Processor<C>
{
    /**
     *
     */
    private boolean enabled = true;

    /**
     *
     */
    private final List<Processor<C>> processors = new LinkedList<>();

    /**
     * Hinzufügen eines {@link Processor} in die Chain.
     *
     * @param processor {@link Processor}
     */
    public void addProcessor(final Processor<C> processor)
    {
        Objects.requireNonNull(processor, "processor required");

        this.processors.add(processor);
    }

    /**
     * @see de.freese.base.core.processor.Processor#execute(java.lang.Object)
     */
    @Override
    public void execute(final C context) throws Exception
    {
        if (!isEnabled())
        {
            return;
        }

        for (Processor<C> processor : this.processors)
        {
            if (!processor.isEnabled())
            {
                continue;
            }

            processor.execute(context);
        }
    }

    /**
     * Liefert den {@link Processor} am Index der {@link ProcessorChain}.
     *
     * @param index int
     * @return {@link Processor}
     */
    public Processor<C> getProcessorAt(final int index)
    {
        return this.processors.get(index);
    }

    /**
     * Liefert die Anzahl der {@link Processor} in der {@link ProcessorChain} .
     *
     * @return int
     */
    public int getSize()
    {
        return this.processors.size();
    }

    /**
     * Liefert den Index des Processors in der {@link ProcessorChain}.
     *
     * @param processor {@link Processor}
     * @return int
     */
    public int indexOf(final Processor<C> processor)
    {
        return this.processors.indexOf(processor);
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
     * Entfernt ein {@link Processor}aus der Chain.
     *
     * @param processor {@link Processor}
     * @return boolean, true, wenn {@link Processor} in der Chain enthalten war
     */
    public boolean removeProcessor(final Processor<C> processor)
    {
        return this.processors.remove(processor);
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
