// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public class LineGraphPainter extends AbstractGraphPainter
{
    /**
    *
    */
    private final Line2D line2d = new Line2D.Float();

    // /**
    // * Color, GradientPaint
    // */
    // private final Paint paint;

    /**
     * Erstellt ein neues {@link LineGraphPainter} Object.
     */
    public LineGraphPainter()
    {
        super();
    }

    // /**
    // * Erstellt ein neues {@link LineGraphPainter} Object.
    // * @param paint {@link Paint}
    // */
    // public LineGraphPainter(final Paint paint)
    // {
    // super();
    //
    // this.paint = Objects.requireNonNull(paint, "paint required");
    // }

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

        float yLast = graphModel.getYKoordinate(values[0], height);

        for (int i = 1; i < values.length; i++)
        {
            float value = values[i];
            // float y = xOffset + (height * value);

            float x = graphModel.getXKoordinate(value, i, width);
            float y = graphModel.getYKoordinate(value, height);

            x += xOffset;
            y = getY(y, height);

            this.line2d.setLine(x - 1, yLast, x, y);
            g.draw(this.line2d);
            // g.drawLine(x - 1, (int) yLast, x, (int) y);

            yLast = y;
        }
    }
}
