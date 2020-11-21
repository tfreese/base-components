// Created: 15.11.2020
package de.freese.base.swing.components.graph.demo.memory;

import java.awt.Color;
import java.net.URL;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Painter;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public final class MemoryGraphDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        MemoryGraphModel graphModel = new MemoryGraphModel();
        Painter<GraphModel> graphPainter = new MemoryGraphPainter();

        MemoryGraphComponent memoryGraph = new MemoryGraphComponent(graphModel, graphPainter, Executors.newScheduledThreadPool(2));
        memoryGraph.setBackground(Color.BLACK);

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
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.getContentPane().add(memoryGraph);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            memoryGraph.start();
        });
    }
}
