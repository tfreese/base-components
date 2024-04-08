package de.freese.base.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

/**
 * Erzeugt mit gerundeten Ecken, Radius ist frei wählbar.
 *
 * @author Thomas Freese
 */
public class CurvedLineBorder extends LineBorder {
    @Serial
    private static final long serialVersionUID = 3562280264054463491L;

    private final int arcSize;

    public CurvedLineBorder(final Color color) {
        this(color, 5);
    }

    public CurvedLineBorder(final Color color, final int arcSize) {
        super(color, 1);

        this.arcSize = arcSize;
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        Insets margin = null;

        if (c instanceof AbstractButton b) {
            margin = b.getMargin();
        }
        else if (c instanceof JToolBar t) {
            margin = t.getMargin();
        }
        else if (c instanceof JTextComponent t) {
            margin = t.getMargin();
        }

        insets.top = (margin != null) ? margin.top : 0;
        insets.left = (margin != null) ? margin.left : 0;
        insets.bottom = (margin != null) ? margin.bottom : 0;
        insets.right = (margin != null) ? margin.right : 0;

        return insets;
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        final Color oldColor = g.getColor();

        g.setColor(getLineColor());
        g.drawRoundRect(x, y, width - 1, height - 1, this.arcSize, this.arcSize);
        g.setColor(oldColor);
    }
}
