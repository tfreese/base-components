// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

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
     *
     */
    private final Supplier<Float> valueSupplier;

    /**
     * Erstellt ein neues {@link BarGraphPainter} Object.
     *
     * @param valueSupplier {@link Supplier}
     */
    public BarGraphPainter(final Supplier<Float> valueSupplier)
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
