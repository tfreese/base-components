// Created: 15.11.2020
package de.freese.base.swing.components.graph;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.swing.components.graph.model.SinusValueSupplier;
import de.freese.base.swing.components.graph.painter.BarGraphPainter;
import de.freese.base.swing.components.graph.painter.LineGraphPainter;

/**
 * @author Thomas Freese
 */
public final class RandomGraphDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomGraphDemo.class);

    public static void main(final String[] args) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        final GraphicsDevice[] gds = ge.getScreenDevices();

        for (GraphicsDevice gd : gds) {
            final boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);

            if (!isPerPixelTranslucencySupported) {
                LOGGER.warn("Per-pixel translucency is not supported on device {}", gd.getIDstring());
                System.exit(0);
            }
        }

        final LineGraphPainter linePainter = new LineGraphPainter() {
            @Override
            protected float getYKoordinate(final float value, final float height) {
                // // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                // return value * height;

                // Sinus: x-Achse auf halber Höhe
                final float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        final DefaultGraphComponent lineGraph = new DefaultGraphComponent(linePainter);
        lineGraph.useBufferedImage(false);

        final BarGraphPainter barPainter = new BarGraphPainter() {
            @Override
            protected float getYKoordinate(final float value, final float height) {
                // // Werte-Bereich: 0 - 1 -> Prozentual umrechnen.
                // return value * height;

                // Sinus: x-Achse auf halber Höhe
                final float middle = height / 2F;

                return (value * middle) + middle;
            }
        };
        final DefaultGraphComponent barGraph = new DefaultGraphComponent(barPainter);
        barGraph.useBufferedImage(false);

        final Supplier<Float> valueSupplier = new SinusValueSupplier();

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            final float value = valueSupplier.get();
            linePainter.getValues().addValue(value);
            barPainter.getValues().addValue(value);

            lineGraph.paintGraph();
            barGraph.paintGraph();
        }, 500, 40, TimeUnit.MILLISECONDS);

        final boolean translucency = false;

        if (translucency) {
            // Sonst kommt Exception: The frame is getDecoratedMap
            JFrame.setDefaultLookAndFeelDecorated(true);
        }

        final JFrame frame = new JFrame("Random Monitor");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);

        if (translucency) {
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setIgnoreRepaint(true);
        }
        else {
            frame.setBackground(Color.BLACK);
        }

        // if (frame.getGraphicsConfiguration().getBufferCapabilities().isPageFlipping()) {
        // try { // no PageFlipping available with opengl
        // final BufferCapabilities cap =
        // new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.BACKGROUND);
        // // ExtendedBufferCapabilities is supposed to do a vsync
        // final ExtendedBufferCapabilities ebc = new ExtendedBufferCapabilities(cap, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
        //
        // if (!VSyncedBSManager.vsyncAllowed(ebc)) {
        // ebc = ebc.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
        // }
        //
        // frame.createBufferStrategy(2, ebc);
        // }
        // catch (AWTException ex) {
        // ex.printStackTrace();
        // }
        // }
        // else {
        // frame.createBufferStrategy(2);
        // }

        frame.setLayout(new GridLayout(2, 1));
        frame.add(lineGraph);
        frame.add(barGraph);
        frame.setSize(600, 800);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private RandomGraphDemo() {
        super();
    }
}
