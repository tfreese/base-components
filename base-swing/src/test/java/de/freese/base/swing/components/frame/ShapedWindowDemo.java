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

/**
 * @author Thomas Freese
 */
public class ShapedWindowDemo extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = 3875661376456849952L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        final boolean isTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);

        // If shaped windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT))
        {
            System.err.println("Shaped windows are not supported");
            System.exit(0);
        }

        // If translucent windows aren't supported,
        // create an opaque window.
        if (!isTranslucencySupported)
        {
            System.out.println("Translucency is not supported, creating an opaque window");
        }

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            ShapedWindowDemo sw = new ShapedWindowDemo();

            // Set the window to 70% translucency, if supported.
            if (isTranslucencySupported)
            {
                sw.setOpacity(0.7f);
            }

            // Display the window.
            sw.setVisible(true);
        });
    }

    /**
     * Erstellt ein neues {@link ShapedWindowDemo} Object.
     */
    public ShapedWindowDemo()
    {
        super("ShapedWindow");
        setLayout(new GridBagLayout());

        // It is best practice to set the window's shape in
        // the componentResized method. Then, if the window
        // changes size, the shape will be correctly recalculated.
        addComponentListener(new ComponentAdapter()
        {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(final ComponentEvent e)
            {
                setShape(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
            }
        });

        setUndecorated(true);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(new JButton("I am a Button"));
    }
}
