// Created: 21.11.2020
package de.freese.base.swing.components.graph.memory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import de.freese.base.swing.components.graph.painter.AbstractGraphPainter;

/**
 * @author Thomas Freese
 */
public class MemoryGraphPainter extends AbstractGraphPainter {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 11);

    private final Line2D line2d = new Line2D.Float();
    private final Color rasterColor = new Color(46, 139, 87);
    private final Rectangle2D rectangle2d = new Rectangle2D.Float();
    private final Runtime runtime;
    private float columnOffset;

    public MemoryGraphPainter() {
        super();

        runtime = Runtime.getRuntime();
    }

    public void generateValue() {
        final float freeMemory = getFreeMemory();
        final float totalMemory = getTotalMemory();

        // Used Memory in %.
        final float value = 1F - (freeMemory / totalMemory);

        getValues().addValue(value);
    }

    @Override
    public void paintGraph(final Graphics2D g, final Component parent, final float width, final float height) {
        final FontMetrics fm = g.getFontMetrics(FONT);
        final int ascent = fm.getAscent();
        final int descent = fm.getDescent();

        final float freeMemory = getFreeMemory();
        final float totalMemory = getTotalMemory();

        g.setColor(Color.GREEN);
        g.setFont(FONT);
        g.drawString((int) totalMemory / 1024 + "K allocated", 4F, ascent - 0.5F);
        g.drawString((int) (totalMemory - freeMemory / 1024) + "K used", 4F, height - descent);

        final float fontHeight = (float) ascent + descent;
        float graphHeight = height - (fontHeight * 2.0F) - 0.5F;

        final float leftInset = 5F;
        final float rightInset = 5F;

        float xOffset = leftInset;
        float yOffset = fontHeight + 0.25F;

        // Linke Balken
        final float blockHeight = graphHeight / 10F;
        final float blockWidth = 20F;
        final int memUsage = (int) ((freeMemory / totalMemory) * 10F);

        g.setColor(new Color(0, 100, 0));

        for (int i = 0; i < memUsage; i++) {
            rectangle2d.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(rectangle2d);
        }

        g.setColor(Color.GREEN);

        for (int i = memUsage; i < 10; i++) {
            rectangle2d.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(rectangle2d);
        }

        // Rand zwischen Blocks und Graph.
        xOffset += blockWidth + leftInset;

        float graphWidth = width - xOffset - rightInset;

        // Rahmen zeichnen.
        g.setColor(Color.RED);
        rectangle2d.setRect(xOffset, yOffset, graphWidth, graphHeight);
        g.draw(rectangle2d);
        // float strokeWidth = ((BasicStroke) g.getStroke()).getLineWidth();
        final float strokeWidth = 1F;
        xOffset += strokeWidth;
        yOffset += strokeWidth;
        graphWidth -= strokeWidth * 2F;
        graphHeight -= strokeWidth * 2F;

        // Raster
        g.translate(xOffset, yOffset);
        paintRaster(g, graphWidth, graphHeight);
        g.translate(-xOffset, -yOffset);

        // Plot zeichnen.
        g.translate(xOffset, yOffset);
        paintPlot(g, graphWidth, graphHeight);
        // g.translate(-xOffset, -yOffset);
    }

    @Override
    protected float getYKoordinate(final float value, final float height) {
        // Prozent-Wert umrechnen.
        return value * height;
    }

    private float getFreeMemory() {
        return runtime.freeMemory();
    }

    private float getTotalMemory() {
        return runtime.totalMemory();
    }

    private void paintPlot(final Graphics2D g, final float width, final float height) {
        final List<Float> values = getValues().getLastValues((int) width);

        if (values.isEmpty()) {
            return;
        }

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        final float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // final float xOffset = 0; // Diagramm von links aufbauen.

        for (int i = 0; i < values.size(); i++) {
            final float value = values.get(i);

            final float x = getXKoordinate(value, i, width);
            final float y = getYKoordinate(value, height);

            rectangle2d.setRect(x + xOffset, height - y, 1, y);

            g.fill(rectangle2d);
        }

        // g.setColor(Color.MAGENTA);
        // rectangle2d.setRect(0, 0, width, height);
        // g.draw(rectangle2d);
    }

    private void paintRaster(final Graphics2D g, final float width, final float height) {
        g.setColor(rasterColor);

        final float rowHeight = height / 10F;
        final float columnWidth = width / 15F;

        for (int row = 1; row < 10; row++) {
            line2d.setLine(0, row * rowHeight, width, row * rowHeight);
            g.draw(line2d);
        }

        if (columnOffset <= 0.0F) {
            columnOffset = columnWidth;
        }

        for (float x = columnOffset; x <= width; x += columnWidth) {
            line2d.setLine(x, 0, x, height);
            g.draw(line2d);
        }

        columnOffset--;
    }
}
