// Created: 24 Juli 2025
package de.freese.base.swing.components.label;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Thomas Freese
 */
public final class AnimatedIconHourGlass implements AnimatedIcon {
    private final ImageIcon[] icons;

    private Icon currentIcon;
    private int index = 0;

    public AnimatedIconHourGlass() {
        this(HourGlassIcons.getHourGlassIconsClockWise());
    }

    public AnimatedIconHourGlass(final ImageIcon[] icons) {
        super();

        this.icons = Objects.requireNonNull(icons, "icons required");
    }

    @Override
    public int getIconHeight() {
        return currentIcon == null ? 0 : currentIcon.getIconHeight();
    }

    @Override
    public int getIconWidth() {
        return currentIcon == null ? 0 : currentIcon.getIconWidth();
    }

    @Override
    public void next() {
        index++;

        if (index == icons.length) {
            index = 0;
        }

        currentIcon = icons[index];
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        if (currentIcon == null) {
            return;
        }

        currentIcon.paintIcon(c, g, x, y);
    }

    @Override
    public void reset() {
        index = 0;
        currentIcon = null;
    }
}
