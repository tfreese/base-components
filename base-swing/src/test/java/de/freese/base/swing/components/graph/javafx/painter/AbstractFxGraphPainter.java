// Created: 27.11.2020
package de.freese.base.swing.components.graph.javafx.painter;

import javafx.scene.canvas.GraphicsContext;

import de.freese.base.swing.components.graph.model.Values;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFxGraphPainter {
    private final Values<Float> values = new Values<>();

    protected AbstractFxGraphPainter() {
        super();
    }

    public Values<Float> getValues() {
        return values;
    }

    public void paint(final GraphicsContext gc, final double width, final double height) {
        configureBackground(gc, width, height);

        paintGraph(gc, width, height);

        // g.dispose(); // Dispose nur wenn man es selbst erzeugt hat.
    }

    public abstract void paintGraph(GraphicsContext gc, double width, double height);

    protected void configureBackground(final GraphicsContext gc, final double width, final double height) {
        gc.clearRect(0, 0, width, height);
    }
}
