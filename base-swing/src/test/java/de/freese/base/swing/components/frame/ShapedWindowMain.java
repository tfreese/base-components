// Created: 11.11.2020
package de.freese.base.swing.components.frame;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html">trans_shaped_windows</a><br>
 * TRANSLUCENT – The underlying platform supports windows with uniform translucency, where each pixel has the same alpha value.<br>
 * PERPIXEL_TRANSLUCENT – The underlying platform supports windows with per-pixel translucency. This capability is required to implement windows that fade
 * away.<br>
 * PERPIXEL_TRANSPARENT – The underlying platform supports shaped windows.<br>
 *
 * @author Thomas Freese
 */
public final class ShapedWindowMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShapedWindowMain.class);

    public static void main(final String[] args) {
        // Determine what the GraphicsDevice can support.
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();

        final boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
        final boolean isTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);

        // If shaped windows aren't supported, exit.
        if (!isPerPixelTranslucencySupported) {
            LOGGER.warn("Per-pixel translucency is not supported on device {}", gd.getIDstring());
            System.exit(0);
        }

        // If translucent windows aren't supported,
        // create an opaque window.
        if (!isTranslucencySupported) {
            LOGGER.warn("Translucency is not supported, creating an opaque window");
        }

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("ShapedWindow");
            frame.setLayout(new GridBagLayout());

            // It is best practice to set the window's shape in
            // the componentResized method. Then, if the window
            // changes size, the shape will be correctly recalculated.
            frame.addComponentListener(new ComponentAdapter() {
                // Give the window an elliptical shape.
                // If the window is resized, the shape is recalculated here.
                @Override
                public void componentResized(final ComponentEvent event) {
                    frame.setShape(new Ellipse2D.Double(0, 0, frame.getWidth(), frame.getHeight()));
                }
            });

            frame.setUndecorated(true);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.add(new JButton("I am a Button"));

            // Set the window to 70% translucency, if supported.
            if (isTranslucencySupported) {
                frame.setOpacity(0.7F);
            }

            // Display the window.
            frame.setVisible(true);
        });
    }

    private ShapedWindowMain() {
        super();
    }
}
