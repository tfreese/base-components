// Created: 15.11.2020
package de.freese.base.swing.components.graph.demo;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.DefaultGraphComponent;
import de.freese.base.swing.components.graph.painter.BarGraphPainter;
import de.freese.base.swing.components.graph.painter.LineGraphPainter;

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
        // Damit beide Diagramme den gleichen Wert darstellen.
        AtomicReference<Float> ref = new AtomicReference<>();

        // Werte-Bereich: 0 - 1
        Supplier<Float> valueSupplier = () -> {

            if (ref.get() == null)
            {
                ref.set((float) Math.random());
            }

            return ref.get();
        };

        DefaultGraphComponent barGraph = new DefaultGraphComponent(new BarGraphPainter(valueSupplier)
        {
            /**
             * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
             */
            @Override
            protected float getYKoordinate(final float value, final float height)
            {
                // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                return value * height;
            }
        });
        barGraph.setBackground(Color.BLACK);

        DefaultGraphComponent lineGraph = new DefaultGraphComponent(new LineGraphPainter(valueSupplier)
        {
            /**
             * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
             */
            @Override
            protected float getYKoordinate(final float value, final float height)
            {
                // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                return value * height;
            }
        });
        lineGraph.setBackground(Color.BLACK);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            lineGraph.paintGraph();
            barGraph.paintGraph();

            SwingUtilities.invokeLater(() -> ref.set(null));
        }, 500, 40, TimeUnit.MILLISECONDS);

        JFrame frame = new JFrame("Random Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);

        frame.setLayout(new GridLayout(2, 1));
        frame.getContentPane().add(lineGraph);
        frame.getContentPane().add(barGraph);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
