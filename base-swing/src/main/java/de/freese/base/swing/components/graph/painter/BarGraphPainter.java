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
    protected void paintGraph(final Graphics2D g, final GraphModel graphModel, final int width, final int height)
    {
        float[] values = graphModel.getValues(width);

        int xOffset = width - values.length; // Diagramm von rechts aufbauen.
        // int xOffset = 0; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        for (int i = 0; i < values.length; i++)
        {
            float value = values[i];

            float x = graphModel.getXKoordinate(value, i, width);
            float y = graphModel.getYKoordinate(value, height);

            x += xOffset;
            y = getY(y, height);

            // g.fillRect(y, 0, 1, (int) y);

            this.rectangle2d.setRect(x, y, 1, height);
            g.fill(this.rectangle2d);
        }
    }
}
