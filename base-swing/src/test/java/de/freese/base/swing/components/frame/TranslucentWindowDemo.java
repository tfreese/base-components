// Created: 11.11.2020
package de.freese.base.swing.components.frame;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * <a href="https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html">trans_shaped_windows</a><br>
 * TRANSLUCENT – The underlying platform supports windows with uniform translucency, where each pixel has the same alpha value.<br>
 * PERPIXEL_TRANSLUCENT – The underlying platform supports windows with per-pixel translucency. This capability is required to implement windows that fade
 * away.<br>
 * PERPIXEL_TRANSPARENT – The underlying platform supports shaped windows.<br>
 *
 * @author Thomas Freese
 */
public class TranslucentWindowDemo extends JFrame
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2882466471490385780L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Determine if the GraphicsDevice supports translucency.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        boolean translucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);

        // If translucent windows aren't supported, exit.
        if (!translucencySupported)
        {
            System.err.println("Translucency is not supported");
            System.exit(0);
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() ->
        {
            TranslucentWindowDemo tw = new TranslucentWindowDemo();

            // Set the window to 55% opaque (45% translucent).
            tw.setOpacity(0.55F);

            // Display the window.
            tw.setVisible(true);
        });
    }

    /**
     * Erstellt ein neues {@link TranslucentWindowDemo} Object.
     */
    public TranslucentWindowDemo()
    {
        super("TranslucentWindow");

        setLayout(new GridBagLayout());

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add a sample button.
        add(new JButton("I am a Button"));
    }
}
