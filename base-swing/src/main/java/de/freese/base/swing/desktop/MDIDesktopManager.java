package de.freese.base.swing.desktop;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane. Used to provide scrollbar functionality.<br>
 * <br>
 * Quelle: http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html?page=1
 *
 * @author Thomas Freese
 */
final class MDIDesktopManager extends DefaultDesktopManager
{
    /**
     *
     */
    private static final long serialVersionUID = 8998750700453491893L;
    /**
     *
     */
    private final MDIDesktopPane desktop;

    /**
     * Erstellt ein neues {@link MDIDesktopManager} Object.
     *
     * @param desktop {@link MDIDesktopPane}
     */
    public MDIDesktopManager(final MDIDesktopPane desktop)
    {
        super();

        this.desktop = desktop;
    }

    /**
     * @see javax.swing.DefaultDesktopManager#endDraggingFrame(javax.swing.JComponent)
     */
    @Override
    public void endDraggingFrame(final JComponent f)
    {
        super.endDraggingFrame(f);

        resizeDesktop();
    }

    /**
     * @see javax.swing.DefaultDesktopManager#endResizingFrame(javax.swing.JComponent)
     */
    @Override
    public void endResizingFrame(final JComponent f)
    {
        super.endResizingFrame(f);

        resizeDesktop();
    }

    /**
     * Liefert die {@link JScrollPane} in der die {@link MDIDesktopPane} liegt.
     *
     * @return {@link JScrollPane}
     */
    private JScrollPane getScrollPane()
    {
        if (this.desktop.getParent()instanceof JViewport v)
        {
            JViewport viewPort = v;

            if (viewPort.getParent()instanceof JScrollPane p)
            {
                return p;
            }
        }

        return null;
    }

    /**
     * Liefert die {@link Insets} der {@link JScrollPane} in der die {@link MDIDesktopPane} liegt.
     *
     * @return {@link Insets}
     */
    private Insets getScrollPaneInsets()
    {
        JScrollPane scrollPane = getScrollPane();

        if (scrollPane == null)
        {
            return new Insets(0, 0, 0, 0);
        }

        return getScrollPane().getBorder().getBorderInsets(scrollPane);
    }

    /**
     *
     */
    public void resizeDesktop()
    {
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();
        int x = 0;
        int y = 0;

        if (scrollPane != null)
        {
            JInternalFrame allFrames[] = this.desktop.getAllFrames();

            for (JInternalFrame allFrame : allFrames)
            {
                if ((allFrame.getX() + allFrame.getWidth()) > x)
                {
                    x = allFrame.getX() + allFrame.getWidth();
                }

                if ((allFrame.getY() + allFrame.getHeight()) > y)
                {
                    y = allFrame.getY() + allFrame.getHeight();
                }
            }

            Dimension d = scrollPane.getVisibleRect().getSize();

            if (scrollPane.getBorder() != null)
            {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            if (x <= d.getWidth())
            {
                x = ((int) d.getWidth()) - 20;
            }

            if (y <= d.getHeight())
            {
                y = ((int) d.getHeight()) - 20;
            }

            this.desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    /**
     *
     */
    public void setNormalSize()
    {
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();
        int width = 0;
        int height = 0;

        if (scrollPane != null)
        {
            Dimension d = scrollPane.getVisibleRect().getSize();

            if (scrollPane.getBorder() != null)
            {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            width = d.width;
            height = d.height;

            this.desktop.setAllSize(width, height);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }
}
