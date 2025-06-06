// Created 10.07.2008
package de.freese.base.core.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class ProcessorChain<C> implements Processor<C> {
    private final List<Processor<C>> processors = new LinkedList<>();

    private boolean enabled = true;

    public void addProcessor(final Processor<C> processor) {
        Objects.requireNonNull(processor, "processor required");

        processors.add(processor);
    }

    @Override
    public void execute(final C context) throws Exception {
        if (!isEnabled()) {
            return;
        }

        for (Processor<C> processor : processors) {
            if (!processor.isEnabled()) {
                continue;
            }

            processor.execute(context);
        }
    }

    public Processor<C> getProcessorAt(final int index) {
        return processors.get(index);
    }

    public int getSize() {
        return processors.size();
    }

    public int indexOf(final Processor<C> processor) {
        return processors.indexOf(processor);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean removeProcessor(final Processor<C> processor) {
        return processors.remove(processor);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
