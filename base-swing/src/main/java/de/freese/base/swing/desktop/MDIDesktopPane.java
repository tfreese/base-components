package de.freese.base.swing.desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * An extension of JDesktopPane that supports often used MDI functionality. This class also handles setting scroll bars for when windows move too far to the
 * left or bottom, providing the MDIDesktopPane is in a ScrollPane.<br>
 * <br>
 * Quelle: http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html?page=1
 *
 * @author Thomas Freese
 */
public final class MDIDesktopPane extends JDesktopPane
{
    /**
     *
     */
    private static final int FRAME_OFFSET = 20;

    /**
     *
     */
    private static final long serialVersionUID = -1067444001367381670L;

    /**
     *
     */
    private final MDIDesktopManager manager;

    /**
     * Erstellt ein neues {@link MDIDesktopPane} Object.
     */
    public MDIDesktopPane()
    {
        super();

        this.manager = new MDIDesktopManager(this);
        setDesktopManager(this.manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    /**
     * Cascade all internal frames.
     */
    public void cascadeFrames()
    {
        cascadeFrames(100, getTitleHeight());
    }

    // /**
    // * @param frame {@link JInternalFrame}
    // * @return {@link Component}
    // * @see java.awt.Container#add(java.awt.Component)
    // */
    // public Component add(final JInternalFrame frame)
    // {
    // JInternalFrame[] allFrames = getAllFrames();
    // Point p = null;
    // int w = 0;
    // int h = 0;
    //
    // Component retval = super.add(frame);
    //
    // checkDesktopSize();
    //
    // if (allFrames.length > 0)
    // {
    // p = allFrames[0].getLocation();
    // p.x = p.x + FRAME_OFFSET;
    // p.y = p.y + FRAME_OFFSET;
    // }
    // else
    // {
    // p = new Point(0, 0);
    // }
    //
    // frame.setLocation(p.x, p.y);
    //
    // if (frame.isResizable())
    // {
    // w = getWidth() - (getWidth() / 3);
    // h = getHeight() - (getHeight() / 3);
    //
    // if (w < frame.getMinimumSize().getWidth())
    // {
    // w = (int) frame.getMinimumSize().getWidth();
    // }
    //
    // if (h < frame.getMinimumSize().getHeight())
    // {
    // h = (int) frame.getMinimumSize().getHeight();
    // }
    //
    // frame.setSize(w, h);
    // }
    //
    // moveToFront(frame);
    // frame.setVisible(true);
    //
    // try
    // {
    // frame.setSelected(true);
    // }
    // catch (PropertyVetoException ex)
    // {
    // frame.toBack();
    // }
    //
    // return retval;
    // }

    /**
     * Cascade all internal frames.
     * 
     * @param minWidth int
     * @param minHeight int
     */
    private void cascadeFrames(final int minWidth, final int minHeight)
    {
        JInternalFrame allFrames[] = getAllFrames();
        int x = 0;
        int y = 0;

        this.manager.setNormalSize();
        int frameHeight = (getHeight() + FRAME_OFFSET) - (allFrames.length * FRAME_OFFSET);
        int frameWidth = (getWidth() + FRAME_OFFSET) - (allFrames.length * FRAME_OFFSET);

        frameHeight = frameHeight < minHeight ? minHeight : frameHeight;
        frameWidth = frameWidth < minWidth ? minWidth : frameWidth;

        for (int i = allFrames.length - 1; i >= 0; i--)
        {
            allFrames[i].setSize(frameWidth, frameHeight);
            allFrames[i].setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }

        checkDesktopSize();
    }

    /**
     * 
     */
    private void checkDesktopSize()
    {
        if ((getParent() != null) && isVisible())
        {
            this.manager.resizeDesktop();
        }
    }

    /**
     * Liefert die Hoehe des Titels.
     * 
     * @return int
     */
    public int getTitleHeight()
    {
        try
        {
            for (JInternalFrame frame : getAllFrames())
            {
                Dimension dimension = ((BasicInternalFrameUI) frame.getUI()).getNorthPane().getPreferredSize();

                return dimension.height;
            }
        }
        catch (Exception ex)
        {
            // Ignore
        }

        return 23;
    }

    /**
     * @see java.awt.Container#remove(java.awt.Component)
     */
    @Override
    public void remove(final Component comp)
    {
        super.remove(comp);

        checkDesktopSize();
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given dimension.
     * 
     * @param d {@link Dimension}
     */
    public void setAllSize(final Dimension d)
    {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given width and height.
     * 
     * @param width int
     * @param height int
     */
    public void setAllSize(final int width, final int height)
    {
        setAllSize(new Dimension(width, height));
    }

    /**
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height)
    {
        super.setBounds(x, y, width, height);

        checkDesktopSize();
    }

    /**
     * Tile all internal frames in flat style.
     */
    public void tileFrames()
    {
        tileFrames(100, getTitleHeight());
    }

    /**
     * Tile all internal frames.
     * 
     * @param minWidth int
     * @param minHeight int
     */
    private void tileFrames(final int minWidth, final int minHeight)
    {
        JInternalFrame allFrames[] = getAllFrames();
        this.manager.setNormalSize();

        // count frames that aren't iconized
        int frameCount = 0;

        for (JInternalFrame frame : allFrames)
        {
            if (!frame.isIcon())
            {
                frameCount++;
            }
        }

        int rows = (int) Math.sqrt(frameCount);
        int cols = frameCount / rows;
        int extra = frameCount % rows;
        // number of columns with an extra row

        int width = getWidth() / cols;
        width = width < minWidth ? minWidth : width;
        int height = getHeight() / rows;
        height = height < minHeight ? minHeight : height;
        int r = 0;
        int c = 0;

        for (JInternalFrame frame : allFrames)
        {
            if (!frame.isIcon())
            {
                try
                {
                    frame.setMaximum(false);
                    frame.reshape(c * width, r * height, width, height);
                    r++;

                    if (r == rows)
                    {
                        r = 0;
                        c++;

                        if (c == (cols - extra))
                        { // start adding an extra row
                            rows++;
                            height = getHeight() / rows;
                        }
                    }
                }
                catch (PropertyVetoException ex)
                {
                    // Ignore
                }
            }
        }

        checkDesktopSize();
    }

    /**
     * Tile all internal frames in flat style.
     */
    public void tileFramesFlat()
    {
        tileFramesFlat(getTitleHeight());
    }

    /**
     * Tile all internal frames in flat style.
     * 
     * @param minHeight int
     */
    private void tileFramesFlat(final int minHeight)
    {
        JInternalFrame allFrames[] = getAllFrames();
        this.manager.setNormalSize();
        int y = 0;

        int frameHeight = getHeight() / allFrames.length;
        frameHeight = frameHeight < minHeight ? minHeight : frameHeight;

        for (JInternalFrame allFrame : allFrames)
        {
            allFrame.setSize(getWidth(), frameHeight);
            allFrame.setLocation(0, y);
            y = y + frameHeight;
        }

        checkDesktopSize();
    }
}
