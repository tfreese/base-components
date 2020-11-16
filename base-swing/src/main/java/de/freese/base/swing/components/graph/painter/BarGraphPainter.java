// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public class BarGraphPainter extends AbstractGraphPainter
{
    /**
     *
     */
    private final Rectangle2D rectangle2d = new Rectangle2D.Float();

    /**
     * Erstellt ein neues {@link BarGraphPainter} Object.
     */
    public BarGraphPainter()
    {
        super();
    }

    /**
     * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D,
     *      de.freese.base.swing.components.graph.model.GraphModel, int, int)
     */
    @Override
    protected void paintGraph(final Graphics2D g, final GraphModel model, final int width, final int height)
    {
        int xStart = width - model.size(); // Diagramm von rechts aufbauen.
        // int xStart = 0; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.GREEN, 0, height, Color.RED));

        for (int i = 0; i < model.size(); i++)
        {
            float yValue = model.getYKoordinate(i, height);

            // g.fillRect(xStart + i, -1, 1, (int) yValue);

            this.rectangle2d.setRect(xStart + i, -1, 1, yValue);
            g.fill(this.rectangle2d);
        }
    }
}
