// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

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
     * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D, java.awt.Component, float, float)
     */
    @Override
    public void paintGraph(final Graphics2D g, final Component parent, final float width, final float height)
    {
        List<Float> values = getLastValues((int) width);

        if (values.isEmpty())
        {
            return;
        }

        float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // float xOffset = 0F; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        for (int i = 0; i < values.size(); i++)
        {
            float value = values.get(i);

            float x = getXKoordinate(value, i, width);
            float y = getYKoordinate(value, height);

            x += xOffset;

            // g.fillRect(y, 0, 1, (int) y);

            this.rectangle2d.setRect(x, y, 1, height);
            g.fill(this.rectangle2d);
        }
    }
}
