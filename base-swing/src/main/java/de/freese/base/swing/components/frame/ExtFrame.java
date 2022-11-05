package de.freese.base.swing.components.frame;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.io.Serial;

import javax.swing.JFrame;

/**
 * {@link JFrame} mit der Möglichkeit per Methode den State zu ändern.<br>
 * deiconify, iconify, maximize, minimize.<br>
 *
 * @author Thomas Freese
 */
public class ExtFrame extends JFrame
{
    @Serial
    private static final long serialVersionUID = 4014880096241781642L;

    public ExtFrame()
    {
        super();
    }

    public ExtFrame(final GraphicsConfiguration gc)
    {
        super(gc);
    }

    public ExtFrame(final String title)
    {
        super(title);
    }

    public ExtFrame(final String title, final GraphicsConfiguration gc)
    {
        super(title, gc);
    }

    public void deiconify()
    {
        int state = getExtendedState();

        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;

        // Deiconify the frame
        setExtendedState(state);
    }

    public void iconify()
    {
        int state = getExtendedState();

        // Set the iconified bit
        state |= Frame.ICONIFIED;

        // Iconify the frame
        setExtendedState(state);
    }

    public void maximize()
    {
        int state = getExtendedState();

        // Set the maximized bits
        state |= Frame.MAXIMIZED_BOTH;

        // Maximize the frame
        setExtendedState(state);
    }

    public void minimize()
    {
        int state = getExtendedState();

        // Clear the maximized bits
        state &= ~Frame.MAXIMIZED_BOTH;

        // Maximize the frame
        setExtendedState(state);
    }
}
