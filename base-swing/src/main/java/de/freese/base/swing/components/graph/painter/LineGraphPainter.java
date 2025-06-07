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
public class LineGraphPainter extends AbstractGraphPainter {
    private final Line2D line2d = new Line2D.Float();

    @Override
    public void paintGraph(final Graphics2D g, final Component parent, final float width, final float height) {
        final List<Float> values = getValues().getLastValues((int) width);

        if (values.isEmpty()) {
            return;
        }

        final float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // float xOffset = 0F; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        // Sinus: x-Achse auf halber Höhe
        final float middle = height / 2F;
        float yLast = middle - (values.getFirst() * middle);

        for (int i = 1; i < values.size(); i++) {
            final float value = values.get(i);
            // float y = xOffset + (height * value);

            final float x = i + xOffset;
            final float y = middle - (value * middle);

            line2d.setLine(x - 1, yLast, x, y);
            g.draw(line2d);
            // g.drawLine(x - 1, (int) yLast, x, (int) y);

            yLast = y;
        }
    }
}
