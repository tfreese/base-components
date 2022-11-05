package de.freese.base.swing.border;

import java.awt.Color;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.Icon;
import javax.swing.border.MatteBorder;

/**
 * Border mit definierbaren Dicken der einzelnen Linien.
 *
 * @author Thomas Freese
 */
public class ExtMatteBorder extends MatteBorder
{
    @Serial
    private static final long serialVersionUID = 823952493901941086L;

    public ExtMatteBorder(final Icon tileIcon)
    {
        super(tileIcon);
    }

    public ExtMatteBorder(final Insets borderInsets, final Color matteColor)
    {
        super(borderInsets, matteColor);
    }

    public ExtMatteBorder(final Insets borderInsets, final Icon tileIcon)
    {
        super(borderInsets, tileIcon);
    }

    public ExtMatteBorder(final int top, final int left, final int bottom, final int right, final Color matteColor)
    {
        super(top, left, bottom, right, matteColor);
    }

    public ExtMatteBorder(final int top, final int left, final int bottom, final int right, final Icon tileIcon)
    {
        super(top, left, bottom, right, tileIcon);
    }

    public void setBottom(final int bottom)
    {
        this.bottom = bottom;
    }

    public void setLeft(final int left)
    {
        this.left = left;
    }

    public void setMatteColor(final Color matteColor)
    {
        this.color = matteColor;
    }

    public void setRight(final int right)
    {
        this.right = right;
    }

    public void setTileIcon(final Icon tileIcon)
    {
        this.tileIcon = tileIcon;
    }

    public void setTop(final int top)
    {
        this.top = top;
    }
}
