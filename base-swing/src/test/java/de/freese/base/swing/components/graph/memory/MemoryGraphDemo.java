// Created: 15.11.2020
package de.freese.base.swing.components.graph.memory;

import java.awt.Color;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class MemoryGraphDemo {
    public static void main(final String[] args) {
        final MemoryGraphComponent memoryGraph = new MemoryGraphComponent(new MemoryGraphPainter(), Executors.newScheduledThreadPool(2));

        final JFrame frame = new JFrame("Memory Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);

        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.add(memoryGraph);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        try {
            URL iconURL = ClassLoader.getSystemResource("icons/memory.gif");

            if (iconURL == null) {
                iconURL = frame.getClass().getResource("icons/memory.gif");
            }

            if (iconURL != null) {
                frame.setIconImage(new ImageIcon(iconURL).getImage());
            }
        }
        catch (Throwable ex) {
            // Empty
        }

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            memoryGraph.start();
        });
    }

    private MemoryGraphDemo() {
        super();
    }
}
