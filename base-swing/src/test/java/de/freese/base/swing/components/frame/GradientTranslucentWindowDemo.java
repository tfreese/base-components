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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html<br>
 * TRANSLUCENT – The underlying platform supports windows with uniform translucency, where each pixel has the same alpha value.<br>
 * PERPIXEL_TRANSLUCENT – The underlying platform supports windows with per-pixel translucency. This capability is required to implement windows that fade
 * away.<br>
 * PERPIXEL_TRANSPARENT – The underlying platform supports shaped windows.<br>
 *
 * @author Thomas Freese
 */
public class GradientTranslucentWindowDemo extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = -368071555470247071L;

    /**
     * @param args String[]
     */
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

        // Sonst kommt Exception: The frame is decorated
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            GradientTranslucentWindowDemo gtw = new GradientTranslucentWindowDemo();

            // Display the window.
            gtw.setVisible(true);
        });
    }

    /**
     * Erstellt ein neues {@link GradientTranslucentWindowDemo} Object.
     */
    public GradientTranslucentWindowDemo()
    {
        super("GradientTranslucentWindow");

        setBackground(new Color(0, 0, 0, 0));
        setSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel()
        {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            /**
             * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
             */
            @Override
            protected void paintComponent(final Graphics g)
            {
                if (g instanceof Graphics2D)
                {
                    final int R = 240;
                    final int G = 240;
                    final int B = 240;

                    Paint paint = new GradientPaint(0.0F, 0.0F, new Color(R, G, B, 0), 0.0F, getHeight(), new Color(R, G, B, 100), true);
                    // Paint paint = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 0), 0.0f, getHeight(), new Color(0, 0, 0, 0), true);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(paint);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        setContentPane(panel);
        setLayout(new GridBagLayout());
        add(new JButton("I am a Button"));
    }
}
