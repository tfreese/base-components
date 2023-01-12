// Created: 04.05.2020
package de.freese.base.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.Serial;

/**
 * Expand the {@link GridBagConstraints} by a Builder-Pattern.
 *
 * @author Thomas Freese
 */
public class GbcBuilder extends GridBagConstraints
{
    @Serial
    private static final long serialVersionUID = 9216852015033867169L;

    /**
     * Defaults:
     * <ul>
     * <li>fill = NONE</li>
     * <li>weightx = 0.0D</li>
     * <li>weighty = 0.0D</li>
     * <li>insets = new Insets(5, 5, 5, 5)</li>
     * </ul>
     */
    public static GbcBuilder of(final int gridx, final int gridy)
    {
        GbcBuilder gbcBuilder = new GbcBuilder(gridx, gridy);
        gbcBuilder.fillNone();
        gbcBuilder.insets(5, 5, 5, 5);

        return gbcBuilder;
    }

    private GbcBuilder(final int gridx, final int gridy)
    {
        super();

        this.gridx = gridx;
        this.gridy = gridy;
    }

    public GbcBuilder anchorCenter()
    {
        this.anchor = CENTER;

        return this;
    }

    public GbcBuilder anchorEast()
    {
        this.anchor = EAST;

        return this;
    }

    public GbcBuilder anchorNorth()
    {
        this.anchor = NORTH;

        return this;
    }

    public GbcBuilder anchorNorthEast()
    {
        this.anchor = NORTHEAST;

        return this;
    }

    public GbcBuilder anchorNorthWest()
    {
        this.anchor = NORTHWEST;

        return this;
    }

    public GbcBuilder anchorSouth()
    {
        this.anchor = SOUTH;

        return this;
    }

    public GbcBuilder anchorWest()
    {
        this.anchor = WEST;

        return this;
    }

    /**
     * Defaults:
     * <ul>
     * <li>weightx = 1.0D</li>
     * <li>weighty = 1.0D</li>
     * </ul>
     */
    public GbcBuilder fillBoth()
    {
        this.fill = BOTH;

        weightx(1.0D);
        weighty(1.0D);

        return this;
    }

    /**
     * Defaults:
     * <ul>
     * <li>weightx = 1.0D</li>
     * <li>weighty = 0.0D</li>
     * </ul>
     */
    public GbcBuilder fillHorizontal()
    {
        this.fill = HORIZONTAL;

        weightx(1.0D);
        weighty(0.0D);

        return this;
    }

    /**
     * Defaults:
     * <ul>
     * <li>weightx = 0.0D</li>
     * <li>weighty = 0.0D</li>
     * </ul>
     */
    public GbcBuilder fillNone()
    {
        this.fill = NONE;

        weightx(0.0D);
        weighty(0.0D);

        return this;
    }

    /**
     * Defaults:
     * <ul>
     * <li>weightx = 0.0D</li>
     * <li>weighty = 1.0D</li>
     * </ul>
     */
    public GbcBuilder fillVertical()
    {
        this.fill = VERTICAL;

        weightx(0.0D);
        weighty(1.0D);

        return this;
    }

    public GbcBuilder gridheight(final int gridheight)
    {
        this.gridheight = gridheight;

        return this;
    }

    public GbcBuilder gridwidth(final int gridwidth)
    {
        this.gridwidth = gridwidth;

        return this;
    }

    public GbcBuilder insets(final Insets insets)
    {
        this.insets = insets;

        return this;
    }

    public GbcBuilder insets(final int top, final int left, final int bottom, final int right)
    {
        this.insets = new Insets(top, left, bottom, right);

        return this;
    }

    public GbcBuilder weightx(final double weightx)
    {
        this.weightx = weightx;

        return this;
    }

    public GbcBuilder weighty(final double weighty)
    {
        this.weighty = weighty;

        return this;
    }
}
