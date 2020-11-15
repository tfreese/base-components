// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Color;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class GraphDemo extends AbstractGraphComponent
{
    /**
     *
     */
    private static final long serialVersionUID = 7470822279989351044L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new JFrame("Memory Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try
        {
            URL iconURL = ClassLoader.getSystemResource("icons/memory.gif");

            if (iconURL == null)
            {
                iconURL = frame.getClass().getResource("icons/memory.gif");
            }

            if (iconURL != null)
            {
                frame.setIconImage(new ImageIcon(iconURL).getImage());
            }
        }
        catch (Throwable ex)
        {
            // Empty
        }

        GraphDemo graphDemo = new GraphDemo();
        graphDemo.setBackground(Color.BLACK);

        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.getContentPane().add(graphDemo);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            graphDemo.setValue((float) Math.random());
            // graphDemo.setValue((float) Runtime.getRuntime().freeMemory() / (float) Runtime.getRuntime().totalMemory());
        }, 500, 40, TimeUnit.MILLISECONDS);
    }

    /**
     * Erstellt ein neues {@link GraphDemo} Object.
     */
    private GraphDemo()
    {
        super();
    }
}
