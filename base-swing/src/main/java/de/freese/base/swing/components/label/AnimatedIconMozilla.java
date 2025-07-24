// Created: 24 Juli 2025
package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * @author Thomas Freese
 */
public final class AnimatedIconMozilla implements AnimatedIcon {
    /**
     * Number of Circles.
     */
    private int circleCount = 8;
    /**
     * Index of the current animated Circle.
     */
    private int circleIndex = -1;
    /**
     * Radius of the hole circle.
     */
    private int circleRadius = 20;
    /**
     * Color of the first circle.
     */
    private Color colorFirst = Color.BLACK;
    /**
     * Color of the last circle.
     */
    private Color colorLast = Color.WHITE;
    /**
     * Length of the tail.<br>
     * Max.: circleCount
     */
    private int trailCount = circleCount;

    @Override
    public int getIconHeight() {
        // return circleIndex == 0 ? 0 : circleRadius;
        return circleRadius + 5;
    }

    @Override
    public int getIconWidth() {
        // return circleIndex == 0 ? 0 : circleRadius;
        return circleRadius + 5;
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

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(colorLast);

        // Center of the animation.
        final int newX = x + (circleRadius / 2) + 5;
        final int newY = y + (circleRadius / 2);

        g.translate(newX, newY);

        final double theta = Math.TAU / circleCount;

        // Radius and Diameter of the small circles.
        final int r = circleRadius / 8;
        final int d = 2 * r;

        for (int index = 0; index < circleCount; index++) {
            g.setColor(calcCircleColor(index));

            g2d.fillOval(r, r, d, d);

            g2d.rotate(theta);
        }

        g.translate(-newX, -newY);
    }

    @Override
    public void reset() {
        circleIndex = -1;
    }

    /**
     * Number of Circles.
     */
    public void setCircleCount(final int circleCount) {
        this.circleCount = circleCount;

        if (trailCount > circleCount) {
            trailCount = circleCount;
        }
    }

    /**
     * Radius of the hole circle.
     */
    public void setCircleRadius(final int circleRadius) {
        this.circleRadius = circleRadius;
    }

    /**
     * Color of the first circle.
     */
    public void setColorFirst(final Color colorFirst) {
        this.colorFirst = colorFirst;
    }

    /**
     * Color of the last circle.
     */
    public void setColorLast(final Color colorLast) {
        this.colorLast = colorLast;
    }

    /**
     * Length of the tail.<br>
     * Max.: circleCount
     */
    public void setTrailCount(final int trailCount) {
        this.trailCount = Math.min(trailCount, circleCount);
    }

    /**
     * Calculate the gradient color for the current animated circle.
     */
    private Color calcCircleColor(final int index) {
        if (index == circleIndex) {
            return colorFirst;
        }

        for (int t = 0; t < trailCount; t++) {
            if (index == (((circleIndex - t) + circleCount) % circleCount)) {
                // Interpolation factor.
                final float factor = (float) (1D - (((double) (trailCount - t)) / (double) trailCount));

                // Merge two colors with an Interpolation factor.
                final float[] aComp = colorFirst.getRGBComponents(null);
                final float[] bComp = colorLast.getRGBComponents(null);
                final float[] cComp = new float[4];

                for (int i = 0; i < 4; i++) {
                    cComp[i] = aComp[i] + ((bComp[i] - aComp[i]) * factor);
                }

                return new Color(cComp[0], cComp[1], cComp[2], cComp[3]);
            }
        }

        return colorLast;
    }
}
