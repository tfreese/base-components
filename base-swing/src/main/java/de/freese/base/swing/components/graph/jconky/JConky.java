// Created: 15.11.2020
package de.freese.base.swing.components.graph.jconky;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.DefaultGraphComponent;
import de.freese.base.swing.components.graph.demo.SinusValueSupplier;
import de.freese.base.swing.components.graph.painter.AbstractGraphPainter;

/**
 * @author Thomas Freese
 */
public final class JConky
{
    /**
     * @author Thomas Freese
     */
    private static final class JConkyPainter extends AbstractGraphPainter
    {
        /**
        *
        */
        private final Rectangle2D rectangle2d = new Rectangle2D.Float();

        /**
         * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#getYKoordinate(float, float)
         */
        @Override
        protected float getYKoordinate(final float value, final float height)
        {
            // Sinus: X-Achse auf halber Höhe
            float middle = height / 2F;

            return (value * middle) + middle;
        }

        /**
         * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D, java.awt.Component, float, float)
         */
        @Override
        protected void paintGraph(final Graphics2D g, final Component parent, final float width, final float height)
        {
            List<Float> values = getLastValues((int) width);

            if (values.isEmpty())
            {
                return;
            }

            float xOffset = width - values.size(); // Diagramm von rechts aufbauen.
            // float xOffset = 0F; // Diagramm von links aufbauen.

            g.setPaint(new GradientPaint(0, 0, Color.RED, 0, height, Color.GREEN));

            for (int i = 0; i < values.size(); i++)
            {
                float value = values.get(i);

                float x = getXKoordinate(value, i, width);
                float y = getYKoordinate(value, height);

                x += xOffset;

                // g.fillRect(y, 0, 1, (int) y);

                this.rectangle2d.setRect(x, y, 1, height);
                g.fill(this.rectangle2d);
            }
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Die Transparenz verschwindet, wenn der Frame auf den 2. Monitor verschoben wird.

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

        Supplier<Float> valueSupplier = new SinusValueSupplier();
        JConkyPainter painter = new JConkyPainter();
        DefaultGraphComponent graph = new DefaultGraphComponent(painter);
        graph.useBufferedImage(false);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            painter.addValue(valueSupplier.get());
            graph.paintGraph();
        }, 500, 40, TimeUnit.MILLISECONDS);

        // jConky immer auf dem 2. Monitor.
        final GraphicsDevice graphicsDevice = gds[1];
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

        // Sonst kommt Exception: The frame is decorated
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("jConky", graphicsConfiguration);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(335, 1060);
        frame.setResizable(true);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setBackground(new Color(0, 0, 0, 75));

        frame.add(graph);

        // Position releativ zum Monitor in der rechten oberen Ecke.
        frame.setLocation(graphicsConfiguration.getBounds().x, 10);
        // frame.setLocation(2700, 10);
        // frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
