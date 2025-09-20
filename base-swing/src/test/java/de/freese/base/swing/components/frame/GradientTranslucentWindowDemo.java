// Created: 11.11.2020
package de.freese.base.swing.components.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
public final class GradientTranslucentWindowDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradientTranslucentWindowDemo.class);

    static void main() {
        // Determine what the GraphicsDevice can support.
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();

        final boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);

        // If translucent windows aren't supported, exit.
        if (!isPerPixelTranslucencySupported) {
            LOGGER.warn("Per-pixel translucency is not supported on device {}", gd.getIDstring());
            System.exit(0);
        }

        // Sonst kommt Exception: The frame is getDecoratedMap
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("GradientTranslucentWindow");
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setSize(new Dimension(600, 400));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            final JPanel panel = new JPanel() {
                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(final Graphics g) {
                    if (g instanceof Graphics2D g2d) {
                        final int R = 240;
                        final int G = 240;
                        final int B = 240;

                        final Paint paint = new GradientPaint(0.0F, 0.0F, new Color(R, G, B, 0), 0.0F, getHeight(), new Color(R, G, B, 100), true);
                        // final Paint paint = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 0), 0.0f, getHeight(), new Color(0, 0, 0, 0), true);
                        g2d.setPaint(paint);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                }
            };

            frame.setContentPane(panel);
            frame.setLayout(new GridBagLayout());
            frame.add(new JButton("I am a Button"));

            // Display the window.
            frame.setVisible(true);
        });
    }

    private GradientTranslucentWindowDemo() {
        super();
    }
}
