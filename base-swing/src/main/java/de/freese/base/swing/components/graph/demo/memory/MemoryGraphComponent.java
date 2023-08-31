// Created: 21.11.2020
package de.freese.base.swing.components.graph.demo.memory;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.freese.base.swing.components.graph.AbstractGraphComponent;

/**
 * @author Thomas Freese
 */
public class MemoryGraphComponent extends AbstractGraphComponent {
    @Serial
    private static final long serialVersionUID = 162498448539283119L;

    private final transient ScheduledExecutorService scheduledExecutorService;

    private transient ScheduledFuture<?> scheduledFuture;

    public MemoryGraphComponent(final MemoryGraphPainter painter, final ScheduledExecutorService scheduledExecutorService) {
        super(painter);

        this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService required");
    }

    public void start() {
        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            ((MemoryGraphPainter) getPainter()).generateValue();
            paintGraph();
        }, 500, 40, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if ((this.scheduledFuture != null)) {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }
    }

    @Override
    protected void onMouseClicked(final MouseEvent event) {
        if (((event.getModifiersEx()) & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            System.gc();
        }
        else if (this.scheduledFuture == null) {
            start();
        }
        else {
            stop();
        }
    }
}
