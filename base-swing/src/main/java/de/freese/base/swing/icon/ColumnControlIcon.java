package de.freese.base.swing.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.plaf.UIResource;

/**
 * Icon für die column-control einer {@link JTable}.
 *
 * @author Thomas Freese
 */
public class ColumnControlIcon implements Icon, UIResource {
    /**
     * @see javax.swing.Icon#getIconHeight()
     */
    @Override
    public int getIconHeight() {
        return 10;
    }

    /**
     * @see javax.swing.Icon#getIconWidth()
     */
    @Override
    public int getIconWidth() {
        return 10;
    }

    /**
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        Color color = c.getForeground();
        g.setColor(color);

        // draw horizontal lines
        g.drawLine(x, y, x + 8, y);
        g.drawLine(x, y + 2, x + 8, y + 2);
        g.drawLine(x, y + 8, x + 2, y + 8);

        // draw vertical lines
        g.drawLine(x, y + 1, x, y + 7);
        g.drawLine(x + 4, y + 1, x + 4, y + 4);
        g.drawLine(x + 8, y + 1, x + 8, y + 4);

        // draw arrow
        g.drawLine(x + 3, y + 6, x + 9, y + 6);
        g.drawLine(x + 4, y + 7, x + 8, y + 7);
        g.drawLine(x + 5, y + 8, x + 7, y + 8);
        g.drawLine(x + 6, y + 9, x + 6, y + 9);
    }
}
