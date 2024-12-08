package de.freese.base.swing.desktop;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane. Used to provide scrollbar functionality.<br>
 * <br>
 * Quelle: <a href="http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html?page=1">jw-05-2001</a>
 *
 * @author Thomas Freese
 */
final class MdiDesktopManager extends DefaultDesktopManager {
    @Serial
    private static final long serialVersionUID = 8998750700453491893L;

    private final MdiDesktopPane desktop;

    MdiDesktopManager(final MdiDesktopPane desktop) {
        super();

        this.desktop = desktop;
    }

    @Override
    public void endDraggingFrame(final JComponent f) {
        super.endDraggingFrame(f);

        resizeDesktop();
    }

    @Override
    public void endResizingFrame(final JComponent f) {
        super.endResizingFrame(f);

        resizeDesktop();
    }

    public void resizeDesktop() {
        final JScrollPane scrollPane = getScrollPane();
        final Insets scrollInsets = getScrollPaneInsets();
        int x = 0;
        int y = 0;

        if (scrollPane != null) {
            final JInternalFrame[] allFrames = desktop.getAllFrames();

            for (JInternalFrame allFrame : allFrames) {
                if ((allFrame.getX() + allFrame.getWidth()) > x) {
                    x = allFrame.getX() + allFrame.getWidth();
                }

                if ((allFrame.getY() + allFrame.getHeight()) > y) {
                    y = allFrame.getY() + allFrame.getHeight();
                }
            }

            final Dimension d = scrollPane.getVisibleRect().getSize();

            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            if (x <= d.getWidth()) {
                x = ((int) d.getWidth()) - 20;
            }

            if (y <= d.getHeight()) {
                y = ((int) d.getHeight()) - 20;
            }

            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    public void setNormalSize() {
        final JScrollPane scrollPane = getScrollPane();
        final Insets scrollInsets = getScrollPaneInsets();
        int width = 0;
        int height = 0;

        if (scrollPane != null) {
            final Dimension d = scrollPane.getVisibleRect().getSize();

            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            width = d.width;
            height = d.height;

            desktop.setAllSize(width, height);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport viewPort
                && viewPort.getParent() instanceof JScrollPane p) {
            return p;
        }

        return null;
    }

    private Insets getScrollPaneInsets() {
        final JScrollPane scrollPane = getScrollPane();

        if (scrollPane == null) {
            return new Insets(0, 0, 0, 0);
        }

        return scrollPane.getBorder().getBorderInsets(scrollPane);
    }
}
