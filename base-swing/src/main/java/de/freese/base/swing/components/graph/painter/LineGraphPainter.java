// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class LineGraphPainter extends AbstractGraphPainter
{
    /**
    *
    */
    private final Line2D line2d = new Line2D.Float();

    /**
     * Erstellt ein neues {@link LineGraphPainter} Object.
     */
    public LineGraphPainter()
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

        float yLast = getYKoordinate(values.get(0), height);

        for (int i = 1; i < values.size(); i++)
        {
            float value = values.get(i);
            // float y = xOffset + (height * value);

            float x = getXKoordinate(value, i, width);
            float y = getYKoordinate(value, height);

            x += xOffset;

            this.line2d.setLine(x - 1, yLast, x, y);
            g.draw(this.line2d);
            // g.drawLine(x - 1, (int) yLast, x, (int) y);

            yLast = y;
        }
    }
}
