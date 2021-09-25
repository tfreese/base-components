// Created: 04.05.2020
package de.freese.base.swing.layout;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Erweitert die {@link GridBagConstraints} um das Builder-Pattern.
 *
 * @author Thomas Freese
 */
public class GbcBuilder extends GridBagConstraints
{
    /**
     *
     */
    private static final long serialVersionUID = 9216852015033867169L;

    /**
     * Erstellt ein neues {@link GbcBuilder} Object.<br>
     * Defaults:
     * <ul>
     * <li>fill = NONE</li>
     * <li>weightx = 0.0D</li>
     * <li>weighty = 0.0D</li>
     * <li>insets = new Insets(5, 5, 5, 5)</li>
     * </ul>
     *
     * @param gridx int
     * @param gridy int
     */
    public GbcBuilder(final int gridx, final int gridy)
    {
        super();

        this.gridx = gridx;
        this.gridy = gridy;

        fillNone();
        insets(5, 5, 5, 5);
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorCenter()
    {
        this.anchor = CENTER;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorEast()
    {
        this.anchor = EAST;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorNorth()
    {
        this.anchor = NORTH;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorNorthEast()
    {
        this.anchor = NORTHEAST;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorNorthWest()
    {
        this.anchor = NORTHWEST;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
    public GbcBuilder anchorSouth()
    {
        this.anchor = SOUTH;

        return this;
    }

    /**
     * @return {@link GbcBuilder}
     */
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
     *
     * @return {@link GbcBuilder}
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
     *
     * @return {@link GbcBuilder}
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
     *
     * @return {@link GbcBuilder}
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
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder fillVertical()
    {
        this.fill = VERTICAL;

        weightx(0.0D);
        weighty(1.0D);

        return this;
    }

    /**
     * @param gridheight int
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder gridheight(final int gridheight)
    {
        this.gridheight = gridheight;

        return this;
    }

    /**
     * @param gridwidth int
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder gridwidth(final int gridwidth)
    {
        this.gridwidth = gridwidth;

        return this;
    }

    /**
     * @param insets {@link Insets}
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder insets(final Insets insets)
    {
        this.insets = insets;

        return this;
    }

    /**
     * @param top int
     * @param left int
     * @param bottom int
     * @param right int
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder insets(final int top, final int left, final int bottom, final int right)
    {
        this.insets = new Insets(top, left, bottom, right);

        return this;
    }

    /**
     * @param weightx double
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder weightx(final double weightx)
    {
        this.weightx = weightx;

        return this;
    }

    /**
     * @param weighty double
     *
     * @return {@link GbcBuilder}
     */
    public GbcBuilder weighty(final double weighty)
    {
        this.weighty = weighty;

        return this;
    }
}
