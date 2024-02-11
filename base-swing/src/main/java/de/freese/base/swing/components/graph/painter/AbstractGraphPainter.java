// Created: 16.11.2020
package de.freese.base.swing.components.graph.painter;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Painter;

import de.freese.base.swing.components.graph.AbstractGraphComponent;
import de.freese.base.swing.components.graph.model.AbstractPainterModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphPainter extends AbstractPainterModel implements Painter<Component> {
    protected AbstractGraphPainter() {
        super();
    }

    @Override
    public void paint(final Graphics2D g, final Component parent, final int width, final int height) {
        configureGraphics(g, parent);
        configureBackground(g, parent, width, height);

        paintGraph(g, parent, width, height);

        // g.dispose(); // Dispose nur wenn man es selbst erzeugt hat.
    }

    public abstract void paintGraph(Graphics2D g, Component parent, float width, float height);

    /**
     * Der Default-Background wird vom Panel/Frame entnommen.
     */
    protected void configureBackground(final Graphics2D g, final Component parent, final int width, final int height) {
        // Paint = Color, GradientPaint, ...

        // final int R = 240;
        // final int G = 240;
        // final int B = 240;
        // GradientPaint translucentPaint = new GradientPaint(0, 0, new Color(R, G, B, 0), 0, height, new Color(R, G, B, 150));
        // g.setPaint(translucentPaint);

        if ((parent instanceof AbstractGraphComponent agc) && agc.isUseBufferedImage()) {
            // Für transparenten Background bei BufferedImage.
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, width, height);

            // Für Foreground bei BufferedImage.
            g.setComposite(AlphaComposite.Src);
        }
        else {
            // Ohne transparenten Background reicht ein clear.
            // g.setBackground(parent.getBackground());
            g.clearRect(0, 0, width, height);
        }
    }

    protected void configureGraphics(final Graphics2D g, final Component parent) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Koordinatenursprung von oben links nach unten links verlegen.
     */
    protected void translateCoordinates(final Graphics2D g, final int height) {
        // Kippt die y-Achse nach oben.
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
