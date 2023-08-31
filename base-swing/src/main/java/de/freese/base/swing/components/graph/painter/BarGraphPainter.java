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
public class BarGraphPainter extends AbstractGraphPainter {
    private final Rectangle2D rectangle2d = new Rectangle2D.Float();

    @Override
    public void paintGraph(final Graphics2D g, final Component parent, final float width, final float height) {
        List<Float> values = getValues().getLastValues((int) width);

        if (values.isEmpty()) {
            return;
        }

        float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // float xOffset = 0F; // Diagramm von links aufbauen.

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        // Sinus: x-Achse auf halber Höhe
        float middle = height / 2F;

        for (int i = 0; i < values.size(); i++) {
            float value = values.get(i);

            float x = i + xOffset;
            float y = Math.abs(value * middle);

            if (value > 0F) {
                this.rectangle2d.setRect(x, middle - y, 1, y);
            }
            else {
                this.rectangle2d.setRect(x, middle, 1, y);
            }

            g.fill(this.rectangle2d);
        }
    }
}
