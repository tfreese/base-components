// Created: 15.11.2020
package de.freese.base.swing.components.graph.demo;

import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.Painter;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.DefaultGraphComponent;
import de.freese.base.swing.components.graph.model.DefaultGraphModel;
import de.freese.base.swing.components.graph.model.GraphModel;
import de.freese.base.swing.components.graph.painter.BarGraphPainter;

/**
 * @author Thomas Freese
 */
public final class RandomGraphDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        DefaultGraphModel graphModel = new DefaultGraphModel(() -> (float) Math.random());
        // Painter<GraphModel> graphPainter = new LineGraphPainter();
        Painter<GraphModel> graphPainter = new BarGraphPainter();

        DefaultGraphComponent memoryGraph = new DefaultGraphComponent(graphModel, graphPainter);
        memoryGraph.setBackground(Color.BLACK);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            graphModel.generateValue();
            memoryGraph.paintGraph();
        }, 100, 40, TimeUnit.MILLISECONDS);

        JFrame frame = new JFrame("Randnom Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.getContentPane().add(memoryGraph);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
