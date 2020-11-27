// Created: 27.11.2020
package de.freese.base.swing.components.graph.javafx.painter;

import de.freese.base.swing.components.graph.model.AbstractPainterModel;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractFxGraphPainter extends AbstractPainterModel
{
    /**
     * Erstellt ein neues {@link AbstractFxGraphPainter} Object.
     */
    public AbstractFxGraphPainter()
    {
        super();
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width int
     * @param height int
     */
    protected void configureBackground(final GraphicsContext gc, final double width, final double height)
    {
        gc.clearRect(0, 0, width, height);
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    public void paint(final GraphicsContext gc, final double width, final double height)
    {
        configureBackground(gc, width, height);

        paintGraph(gc, width, height);

        // g.dispose(); // Dispose nur wenn man es selbst erzeugt hat.
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    public abstract void paintGraph(final GraphicsContext gc, final double width, final double height);
}
