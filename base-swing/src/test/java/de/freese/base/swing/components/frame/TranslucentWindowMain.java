// Created: 11.11.2020
package de.freese.base.swing.components.frame;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;

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
public final class TranslucentWindowMain {
    public static void main(final String[] args) {
        // Determine if the GraphicsDevice supports translucency.
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();

        final boolean translucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);

        // If translucent windows aren't supported, exit.
        if (!translucencySupported) {
            System.err.println("Translucency is not supported");
            System.exit(0);
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("TranslucentWindow");
            frame.setLayout(new GridBagLayout());

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Add a sample button.
            frame.add(new JButton("I am a Button"));

            // Set the window to 55% opaque (45% translucent).
            frame.setOpacity(0.55F);

            // Display the window.
            frame.setVisible(true);
        });
    }

    private TranslucentWindowMain() {
        super();
    }
}
