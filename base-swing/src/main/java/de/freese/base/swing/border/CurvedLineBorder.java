package de.freese.base.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

/**
 * Erzeugt mit gerundeten Ecken, Radius ist frei waehlbar.
 *
 * @author Thomas Freese
 */
public class CurvedLineBorder extends LineBorder
{
    /**
     *
     */
    private static final long serialVersionUID = 3562280264054463491L;

    /**
     *
     */
    private final int arcSize;

    /**
     * Creates a new {@link CurvedLineBorder} object.
     * 
     * @param color {@link Color}
     */
    public CurvedLineBorder(final Color color)
    {
        this(color, 5);
    }

    /**
     * Creates a new {@link CurvedLineBorder} object.
     * 
     * @param color {@link Color}
     * @param arcSize int
     */
    public CurvedLineBorder(final Color color, final int arcSize)
    {
        super(color, 1);

        this.arcSize = arcSize;
    }

    /**
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(final Component c)
    {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    /**
     * @see javax.swing.border.LineBorder#getBorderInsets(java.awt.Component, java.awt.Insets)
     */
    @Override
    public Insets getBorderInsets(final Component c, final Insets insets)
    {
        Insets margin = null;

        if (c instanceof AbstractButton)
        {
            AbstractButton b = (AbstractButton) c;

            margin = b.getMargin();
        }
        else if (c instanceof JToolBar)
        {
            JToolBar t = (JToolBar) c;

            margin = t.getMargin();
        }
        else if (c instanceof JTextComponent)
        {
            JTextComponent t = (JTextComponent) c;

            margin = t.getMargin();
        }

        insets.top = (margin != null) ? margin.top : 0;
        insets.left = (margin != null) ? margin.left : 0;
        insets.bottom = (margin != null) ? margin.bottom : 0;
        insets.right = (margin != null) ? margin.right : 0;

        return insets;
    }

    /**
     * @see javax.swing.border.LineBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height)
    {
        Color oldColor = g.getColor();

        g.setColor(getLineColor());
        g.drawRoundRect(x, y, width - 1, height - 1, this.arcSize, this.arcSize);
        g.setColor(oldColor);
    }
}
