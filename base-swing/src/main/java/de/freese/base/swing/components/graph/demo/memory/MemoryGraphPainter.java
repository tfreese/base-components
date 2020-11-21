// Created: 21.11.2020
package de.freese.base.swing.components.graph.demo.memory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import de.freese.base.swing.components.graph.model.GraphModel;
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
    private final Rectangle2D rectangle2d = new Rectangle2D.Float();

    /**
    *
    */
    private final Rectangle2D rectangle2dFreeMemory = new Rectangle2D.Float();

    /**
    *
    */
    private final Rectangle2D rectangle2dUsedMemory = new Rectangle2D.Float();

    /**
     * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D,
     *      de.freese.base.swing.components.graph.model.GraphModel, int, int)
     */
    @Override
    protected void paintGraph(final Graphics2D g, final GraphModel graphModel, final int width, final int height)
    {
        MemoryGraphModel memoryGraphModel = (MemoryGraphModel) graphModel;

        FontMetrics fm = g.getFontMetrics(FONT);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        float freeMemory = memoryGraphModel.getFreeMemory();
        float totalMemory = memoryGraphModel.getTotalMemory();

        g.setColor(Color.GREEN);
        g.setFont(FONT);
        g.drawString(String.valueOf((int) totalMemory / 1024) + "K allocated", 4F, ascent - 0.5F);
        g.drawString(String.valueOf((int) (totalMemory - freeMemory) / 1024) + "K used", 4, height - descent);

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
            this.rectangle2dFreeMemory.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.rectangle2dFreeMemory);
        }

        g.setColor(Color.GREEN);

        for (int i = memUsage; i < 10; i++)
        {
            this.rectangle2dUsedMemory.setRect(xOffset, fontHeight + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.rectangle2dUsedMemory);
        }

        // Rand zwischen Blocks und Graph.
        xOffset += blockWidth + leftInset;

        float graphWidth = width - xOffset - rightInset;

        // Rahmen zeichnen.
        // g.setColor(Color.RED);
        // this.rectangle2d.setRect(xOffset, yOffset, graphWidth, graphHeight);
        // g.draw(this.rectangle2d);
        // graphWidth -= 1;
        // xOffset += 1;
        // yOffset += 1;

        float[] values = graphModel.getValues((int) graphWidth);

        xOffset += graphWidth - values.length;

        g.setPaint(new GradientPaint(0, yOffset, Color.RED, 0, height - yOffset, Color.GREEN));

        for (int i = 0; i < values.length; i++)
        {
            float value = values[i];

            float x = graphModel.getXKoordinate(value, i, width);
            float y = graphModel.getYKoordinate(value, graphHeight);

            x += xOffset;
            // y = getY(y, graphHeight) + yOffset;
            //
            // this.rectangle2d.setRect(x, y, 1, height - yOffset - y);

            this.rectangle2d.setRect(x, height - yOffset - y, 1, y);

            g.fill(this.rectangle2d);
        }
    }
}
