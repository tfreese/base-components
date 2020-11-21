// Created: 21.11.2020
package de.freese.base.swing.components.graph.demo.memory;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.Painter;
import de.freese.base.swing.components.graph.AbstractGraphComponent;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public class MemoryGraphComponent extends AbstractGraphComponent
{
    /**
     *
     */
    private static final long serialVersionUID = 162498448539283119L;

    /**
    *
    */
    private transient ScheduledExecutorService scheduledExecutorService;

    /**
    *
    */
    private transient ScheduledFuture<?> scheduledFuture;

    /**
     * Erstellt ein neues {@link MemoryGraphComponent} Object.
     *
     * @param graphModel {@link GraphModel}
     * @param painter {@link Painter}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     */
    public MemoryGraphComponent(final GraphModel graphModel, final Painter<GraphModel> painter, final ScheduledExecutorService scheduledExecutorService)
    {
        super(graphModel, painter);

        this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService required");
    }

    /**
     * @see de.freese.base.swing.components.graph.AbstractGraphComponent#onMouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    protected void onMouseClicked(final MouseEvent event)
    {
        if (((event.getModifiersEx()) & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)
        {
            System.gc();
        }
        else if (this.scheduledFuture == null)
        {
            start();
        }
        else
        {
            stop();
        }
    }

    /**
     *
     */
    public void start()
    {
        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            getGraphModel().generateValue();
            paintGraph();
        }, 100, 40, TimeUnit.MILLISECONDS);
    }

    /**
     *
     */
    public void stop()
    {
        if ((this.scheduledFuture != null))
        {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }
    }
}
