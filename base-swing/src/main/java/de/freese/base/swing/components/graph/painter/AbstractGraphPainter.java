// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import javax.swing.Painter;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphPainter implements Painter<GraphModel>
{
    /**
     * Color, GradientPaint
     */
    private Paint backgroundPaint;

    /**
     * Erstellt ein neues {@link AbstractGraphPainter} Object.
     */
    public AbstractGraphPainter()
    {
        super();
    }

    /**
     * @param g {@link Graphics2D}
     * @param width int
     * @param height int
     */
    protected void configureBackground(final Graphics2D g, final int width, final int height)
    {
        // final int R = 240;
        // final int G = 240;
        // final int B = 240;
        // GradientPaint translucentPaint = new GradientPaint(0, 0, new Color(R, G, B, 0), 0, height, new Color(R, G, B, 150));
        // g.setPaint(translucentPaint);

        if (getBackgroundPaint() != null)
        {
            g.setPaint(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, width, height);
        }
        else
        {
            g.setBackground(Color.BLACK);
            g.clearRect(0, 0, width, height);
        }
    }

    /**
     * @param g {@link Graphics2D}
     */
    protected void configureGraphics(final Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * @return {@link Paint}
     */
    protected Paint getBackgroundPaint()
    {
        return this.backgroundPaint;
    }

    /**
     * @see javax.swing.Painter#paint(java.awt.Graphics2D, java.lang.Object, int, int)
     */
    @Override
    public void paint(final Graphics2D g, final GraphModel model, final int width, final int height)
    {
        if (model.size() == 0)
        {
            return;
        }

        translateCoordinates(g, height);
        configureGraphics(g);
        configureBackground(g, width, height);

        paintGraph(g, model, width, height);

        g.dispose();
    }

    /**
     * @param g {@link Graphics2D}
     * @param model {@link GraphModel}
     * @param width int
     * @param height int
     */
    protected abstract void paintGraph(final Graphics2D g, final GraphModel model, final int width, final int height);

    /**
     * Color, GradientPaint
     *
     * @param backgroundPaint {@link Paint}
     */
    public void setBackgroundPaint(final Paint backgroundPaint)
    {
        this.backgroundPaint = backgroundPaint;
    }

    /**
     * Koordinatenursprung von oben links nach unten links verlegen.
     *
     * @param g {@link Graphics2D}
     * @param height int
     */
    protected void translateCoordinates(final Graphics2D g, final int height)
    {
        // Kippt die Y-Achse nach oben.
        g.scale(1.0D, -1.0D);

        // Verschiebt die 0-0 Koordinate nach unten.
        g.translate(0, -height);
    }
}

// GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO, model.size());
// path.moveTo(xStart, model.getYKoordinate(0, height));
//
// for (int i = 1; i < model.size(); i++)
// {
// float yValue = model.getYKoordinate(i, height);
//
// path.lineTo(xStart + i, yValue);
// }
//
// // path.closePath();
//
// g.fill(path);

// int[] x = new int[model.size()];
// int[] y = new int[model.size()];
//
// for (int i = 0; i < model.size(); i++)
// {
// x[i] = xStart + i;
// y[i] = (int) model.getYKoordinate(i, height);
// }
//
// Polygon polygon = new Polygon(x, y, model.size());
// g.draw(polygon);
