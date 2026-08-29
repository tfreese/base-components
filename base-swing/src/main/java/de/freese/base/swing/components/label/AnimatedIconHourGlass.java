package de.freese.base.swing.components.label;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.ImageIcon;

/**
 * @author Thomas Freese
 * @since 24.07.2025
 */
public final class AnimatedIconHourGlass implements AnimatedIcon {
    private final ImageIcon[] icons;

    private int index = -1;

    public AnimatedIconHourGlass() {
        this(HourGlassIcons.getHourGlassIconsClockWise());
    }

    public AnimatedIconHourGlass(final ImageIcon[] icons) {
        Objects.requireNonNull(icons, "icons required");

        super();

        if (icons.length == 0) {
            throw new IllegalArgumentException("array is empty");
        }

        this.icons = Arrays.copyOf(icons, icons.length);
    }

    @Override
    public int getIconHeight() {
        return icons[0].getIconHeight();
    }

    @Override
    public int getIconWidth() {
        return icons[0].getIconWidth();
    }

    @Override
    public void next() {
        index++;

        if (index == icons.length) {
            index = 0;
        }
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        if (index < 0) {
            return;
        }

        icons[index].paintIcon(c, g, x, y);
    }

    @Override
    public void reset() {
        index = -1;
    }
}
