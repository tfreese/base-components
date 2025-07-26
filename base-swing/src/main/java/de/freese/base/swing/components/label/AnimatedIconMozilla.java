// Created: 24 Juli 2025
package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.UIManager;

/**
 * @author Thomas Freese
 */
public final class AnimatedIconMozilla implements AnimatedIcon {
    private Color[] circleColors;
    private int circleCount = 8;
    private int circleIndex = -1;
    private Color colorFirst = Color.BLACK;
    private Color colorLast = UIManager.getColor("Panel.background");
    private int iconSize = 20;
    private int trailCount = 7;

    @Override
    public int getIconHeight() {
        // return circleIndex == 0 ? 0 : circleRadius;
        return iconSize + 5;
    }

    @Override
    public int getIconWidth() {
        // return circleIndex == 0 ? 0 : circleRadius;
        return iconSize + 5;
    }

    @Override
    public void next() {
        circleIndex++;

        if (circleIndex == circleCount) {
            circleIndex = 0;
        }
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        if (circleIndex < 0) {
            return;
        }

        if (circleColors == null) {
            generateCircleColors();
        }

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Change the center of the animation.
        final int newX = x + (iconSize / 2) + 5;
        final int newY = y + (iconSize / 2);

        g.translate(newX, newY);

        final double theta = Math.TAU / circleCount;

        // Radius and Diameter of the small circles.
        final int r = iconSize / 8;
        final int d = 2 * r;

        for (int index = 0; index < circleCount; index++) {
            final Color color = circleColors[(((circleIndex - index) + circleCount) % circleCount)];
            g.setColor(color);

            g2d.fillOval(r, r, d, d);

            g2d.rotate(theta);
        }

        // g.setColor(colorLast);

        g.translate(-newX, -newY);
    }

    @Override
    public void reset() {
        circleIndex = -1;
    }

    /**
     * Number of Circles.<br>
     * Default: 8
     */
    public void setCircleCount(final int circleCount) {
        this.circleCount = circleCount;

        circleColors = null;
    }

    /**
     * Color of the first circle.<br>
     * Default: <code>Color.BLACK</code>
     */
    public void setColorFirst(final Color colorFirst) {
        this.colorFirst = colorFirst;

        circleColors = null;
    }

    /**
     * Color of the last circle.<br>
     * Default: <code>UIManager.getColor("Panel.background")</code>
     */
    public void setColorLast(final Color colorLast) {
        this.colorLast = colorLast;

        circleColors = null;
    }

    /**
     * Size of the Icon.<br>
     * Default: 20
     */
    public void setIconSize(final int iconSize) {
        this.iconSize = iconSize;
    }

    /**
     * Length of the tail.<br>
     * Cannot be greater than circleCount.<br>
     * Default: 7
     */
    public void setTrailCount(final int trailCount) {
        this.trailCount = Math.min(trailCount, circleCount);

        circleColors = null;
    }

    /**
     * Calculate the gradient colors for circles.
     */
    private void generateCircleColors() {
        circleColors = new Color[circleCount];

        final float[] rgbFirst = colorFirst.getRGBComponents(null);
        final float[] rgbLast = colorLast.getRGBComponents(null);

        final double percent = 1D / (trailCount - 1);

        for (int i = 0; i < circleColors.length; i++) {
            if (i < trailCount) {
                // Merge two colors with an Interpolation factor.
                final double factor = i * percent;

                final double[] rgbMerged = new double[4];

                for (int c = 0; c < 4; c++) {
                    rgbMerged[c] = rgbFirst[c] + (((double) rgbLast[c] - rgbFirst[c]) * factor);
                }

                circleColors[i] = new Color((float) rgbMerged[0], (float) rgbMerged[1], (float) rgbMerged[2], (float) rgbMerged[3]);
            }
            else {
                // Empty/invisible circles.
                circleColors[i] = UIManager.getColor("Panel.background");
            }
        }
    }
}
