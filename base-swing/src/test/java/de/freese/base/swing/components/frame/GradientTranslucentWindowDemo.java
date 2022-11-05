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

/**
 * <a href="https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html">trans_shaped_windows</a><br>
 * TRANSLUCENT – The underlying platform supports windows with uniform translucency, where each pixel has the same alpha value.<br>
 * PERPIXEL_TRANSLUCENT – The underlying platform supports windows with per-pixel translucency. This capability is required to implement windows that fade
 * away.<br>
 * PERPIXEL_TRANSPARENT – The underlying platform supports shaped windows.<br>
 *
 * @author Thomas Freese
 */
public class GradientTranslucentWindowDemo
{
    public static void main(final String[] args)
    {
        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);

        // If translucent windows aren't supported, exit.
        if (!isPerPixelTranslucencySupported)
        {
            System.out.println("Per-pixel translucency is not supported");
            System.exit(0);
        }

        // Sonst kommt Exception: The frame is getDecoratedMap
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("GradientTranslucentWindow");
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setSize(new Dimension(600, 400));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel panel = new JPanel()
            {
                /**
                 *
                 */
                @Serial
                private static final long serialVersionUID = 1L;

                /**
                 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
                 */
                @Override
                protected void paintComponent(final Graphics g)
                {
                    if (g instanceof Graphics2D g2d)
                    {
                        final int R = 240;
                        final int G = 240;
                        final int B = 240;

                        Paint paint = new GradientPaint(0.0F, 0.0F, new Color(R, G, B, 0), 0.0F, getHeight(), new Color(R, G, B, 100), true);
                        // Paint paint = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 0), 0.0f, getHeight(), new Color(0, 0, 0, 0), true);
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
}
