// Created: 21.11.2020
package de.freese.base.swing.components.graph.demo.memory;

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
public class MemoryGraphPainter extends AbstractGraphPainter
{
    /**
     *
     */
    private static final Font FONT = new Font("Arial", Font.PLAIN, 11);

    /**
    *
    */
    private float columnOffset;

    /**
    *
    */
    private final Line2D line2d = new Line2D.Float();

    /**
     *
     */
    private final Color rasterColor = new Color(46, 139, 87);

    /**
     *
     */
    private final Rectangle2D rectangle2d = new Rectangle2D.Float();

    /**
    *
    */
    private final Runtime runtime;

    /**
     * Erstellt ein neues {@link MemoryGraphPainter} Object.
     */
    public MemoryGraphPainter()
    {
        super();

        this.runtime = Runtime.getRuntime();
    }

    /**
     *
     */
    public void generateValue()
    {
        float freeMemory = getFreeMemory();
        float totalMemory = getTotalMemory();

        // Used Memory in %.
        float value = 1F - (freeMemory / totalMemory);

        addValue(value);
    }

    /**
     * @return float
     */
    private float getFreeMemory()
    {
        return this.runtime.freeMemory();
    }

    /**
     * @return float
     */
    private float getTotalMemory()
    {
        return this.runtime.totalMemory();
    }

    /**
     * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
     */
    @Override
    protected float getYKoordinate(final float value, final float height)
    {
        // Prozent-Wert umrechnen.
        float y = value * height;

        return y;
    }

    /**
     * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D, java.awt.Component, float, float)
     */
    @Override
    protected void paintGraph(final Graphics2D g, final Component parent, final float width, final float height)
    {
        FontMetrics fm = g.getFontMetrics(FONT);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        float freeMemory = getFreeMemory();
        float totalMemory = getTotalMemory();

        g.setColor(Color.GREEN);
        g.setFont(FONT);
        g.drawString(String.valueOf((int) totalMemory / 1024) + "K allocated", 4F, ascent - 0.5F);
        g.drawString(String.valueOf((int) (totalMemory - freeMemory) / 1024) + "K used", 4F, height - descent);

        float fontHeight = (float) ascent + descent;
        float graphHeight = height - (fontHeight * 2.0F) - 0.5F;

        float leftInset = 5F;
        float rightInset = 5F;

        float xOffset = leftInset;
        float yOffset = fontHeight + 0.25F;

        // Linke Balken
        float blockHeight = graphHeight / 10F;
        float blockWidth = 20F;
        int memUsage = (int) ((freeMemory / totalMemory) * 10F);

        g.setColor(new Color(0, 100, 0));

        for (int i = 0; i < memUsage; i++)
        {
            this.rectangle2d.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.rectangle2d);
        }

        g.setColor(Color.GREEN);

        for (int i = memUsage; i < 10; i++)
        {
            this.rectangle2d.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.rectangle2d);
        }

        // Rand zwischen Blocks und Graph.
        xOffset += blockWidth + leftInset;

        float graphWidth = width - xOffset - rightInset;

        // Rahmen zeichnen.
        g.setColor(Color.RED);
        this.rectangle2d.setRect(xOffset, yOffset, graphWidth, graphHeight);
        g.draw(this.rectangle2d);
        // float strokeWidth = ((BasicStroke) g.getStroke()).getLineWidth();
        float strokeWidth = 1F;
        xOffset += strokeWidth;
        yOffset += strokeWidth;
        graphWidth -= (strokeWidth * 2F);
        graphHeight -= (strokeWidth * 2F);

        // Raster
        g.translate(xOffset, yOffset);
        paintRaster(g, graphWidth, graphHeight);
        g.translate(-xOffset, -yOffset);

        // Plot zeichnen.
        g.translate(xOffset, yOffset);
        paintPlot(g, graphWidth, graphHeight);
        // g.translate(-xOffset, -yOffset);
    }

    /**
     * @param g {@link Graphics2D}
     * @param width float
     * @param height float
     */
    private void paintPlot(final Graphics2D g, final float width, final float height)
    {
        List<Float> values = getLastValues((int) width);

        if (values.isEmpty())
        {
            return;
        }

        g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

        float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // float xOffset = 0; // Diagramm von links aufbauen.

        for (int i = 0; i < values.size(); i++)
        {
            float value = values.get(i);

            float x = getXKoordinate(value, i, width);
            float y = getYKoordinate(value, height);

            this.rectangle2d.setRect(x + xOffset, height - y, 1, y);

            g.fill(this.rectangle2d);
        }

        // g.setColor(Color.MAGENTA);
        // this.rectangle2d.setRect(0, 0, width, height);
        // g.draw(this.rectangle2d);
    }

    /**
     * @param g {@link Graphics2D}
     * @param width float
     * @param height float
     */
    private void paintRaster(final Graphics2D g, final float width, final float height)
    {
        g.setColor(this.rasterColor);

        float rowHeight = height / 10F;
        float columnWidth = width / 15F;

        for (int row = 1; row < 10; row++)
        {
            this.line2d.setLine(0, row * rowHeight, width, row * rowHeight);
            g.draw(this.line2d);
        }

        if (this.columnOffset <= 0.0F)
        {
            this.columnOffset = columnWidth;
        }

        for (float x = this.columnOffset; x <= width; x += columnWidth)
        {
            this.line2d.setLine(x, 0, x, height);
            g.draw(this.line2d);
        }

        this.columnOffset--;
    }
}
