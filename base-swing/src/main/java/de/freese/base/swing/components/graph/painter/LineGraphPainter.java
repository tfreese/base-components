// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

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
    *
    */
    private final Supplier<Float> valueSupplier;

    /**
     * Erstellt ein neues {@link LineGraphPainter} Object.
     *
     * @param valueSupplier {@link Supplier}
     */
    public LineGraphPainter(final Supplier<Float> valueSupplier)
    {
        super();

        this.valueSupplier = Objects.requireNonNull(valueSupplier, "valueSupplier required");
    }

    /**
     * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#generateValue(int)
     */
    @Override
    protected void generateValue(final int width)
    {
        float value = this.valueSupplier.get();

        addValue(value, width);
    }

    /**
     * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D, java.awt.Component, float, float)
     */
    @Override
    protected void paintGraph(final Graphics2D g, final Component parent, final float width, final float height)
    {
        List<Float> values = getLastValues((int) width);

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