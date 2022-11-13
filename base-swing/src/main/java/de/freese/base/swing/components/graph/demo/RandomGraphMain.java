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
public final class RandomGraphMain
{
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

                // Sinus: x-Achse auf halber Höhe
                float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        DefaultGraphComponent lineGraph = new DefaultGraphComponent(linePainter);
        lineGraph.useBufferedImage(false);

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

                // Sinus: x-Achse auf halber Höhe
                float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        DefaultGraphComponent barGraph = new DefaultGraphComponent(barPainter);
        barGraph.useBufferedImage(false);

        Supplier<Float> valueSupplier = new SinusValueSupplier();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            float value = valueSupplier.get();
            linePainter.getValues().addValue(value);
            barPainter.getValues().addValue(value);

            lineGraph.paintGraph();
            barGraph.paintGraph();
        }, 500, 40, TimeUnit.MILLISECONDS);

        boolean translucency = false;

        if (translucency)
        {
            // Sonst kommt Exception: The frame is getDecoratedMap
            JFrame.setDefaultLookAndFeelDecorated(true);
        }

        JFrame frame = new JFrame("Random Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);

        if (translucency)
        {
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setIgnoreRepaint(true);
        }
        else
        {
            frame.setBackground(Color.BLACK);
        }

        // if (frame.getGraphicsConfiguration().getBufferCapabilities().isPageFlipping())
        // {
        // try
        // { // no PageFlipping available with opengl
        // BufferCapabilities cap =
        // new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.BACKGROUND);
        // // ExtendedBufferCapabilities is supposed to do a vsync
        // ExtendedBufferCapabilities ebc = new ExtendedBufferCapabilities(cap, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
        //
        // if (!VSyncedBSManager.vsyncAllowed(ebc))
        // {
        // ebc = ebc.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
        // }
        //
        // frame.createBufferStrategy(2, ebc);
        // }
        // catch (AWTException ex)
        // {
        // ex.printStackTrace();
        // }
        // }
        // else
        // {
        // frame.createBufferStrategy(2);
        // }

        frame.setLayout(new GridLayout(2, 1));
        frame.add(lineGraph);
        frame.add(barGraph);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private RandomGraphMain()
    {
        super();
    }
}
