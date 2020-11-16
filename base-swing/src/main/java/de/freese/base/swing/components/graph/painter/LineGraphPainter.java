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
    protected void paintGraph(final Graphics2D g, final GraphModel model, final int width, final int height)
    {
        int xStart = width - model.size(); // Diagramm von rechts aufbauen.
        // int xStart = 0; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.GREEN, 0, height, Color.RED));

        float yValueLast = model.getYKoordinate(0, height);

        for (int i = 1; i < model.size(); i++)
        {
            // float yValue = yStart + (graphHeight * getValues().get(i));

            float yValue = model.getYKoordinate(i, height);

            this.line2d.setLine((xStart + i) - 1, yValueLast, xStart + i, yValue);
            g.draw(this.line2d);
            // g.drawLine((xStart + i) - 1, (int) yValueLast, xStart + i, (int) yValue);

            yValueLast = yValue;
        }
    }
}
