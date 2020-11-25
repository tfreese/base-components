// Created: 15.11.2020
package de.freese.base.swing.components.graph.demo;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] gds = ge.getScreenDevices();

        for (GraphicsDevice gd : gds)
        {
            boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);

            if (!isPerPixelTranslucencySupported)
            {
                System.out.println("Per-pixel translucency is not supported on device " + gd.getIDstring());
                System.exit(0);
            }
        }

        LineGraphPainter linePainter = new LineGraphPainter()
        {
            /**
             * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
             */
            @Override
            protected float getYKoordinate(final float value, final float height)
            {
                // // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                // return value * height;

                // Sinus: X-Achse auf halber Höhe
                float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        DefaultGraphComponent lineGraph = new DefaultGraphComponent(linePainter);

        BarGraphPainter barPainter = new BarGraphPainter()
        {
            /**
             * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
             */
            @Override
            protected float getYKoordinate(final float value, final float height)
            {
                // // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                // return value * height;

                // Sinus: X-Achse auf halber Höhe
                float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        DefaultGraphComponent barGraph = new DefaultGraphComponent(barPainter);

        Supplier<Float> valueSupplier = new SinusValueSupplier();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            float value = valueSupplier.get();
            linePainter.addValue(value);
            barPainter.addValue(value);

            lineGraph.paintGraph();
            barGraph.paintGraph();
        }, 500, 40, TimeUnit.MILLISECONDS);

        JFrame frame = new JFrame("Random Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.setBackground(Color.BLACK);

        frame.setLayout(new GridLayout(2, 1));
        frame.add(lineGraph);
        frame.add(barGraph);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
