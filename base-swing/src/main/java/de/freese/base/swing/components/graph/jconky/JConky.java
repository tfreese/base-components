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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.DefaultGraphComponent;
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
         * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#configureBackground(java.awt.Graphics2D, int, int)
         */
        @Override
        protected void configureBackground(final Graphics2D g, final int width, final int height)
        {
            // Paint paint = new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 0), 0.0F, height, new Color(100, 100, 100, 100), true);
            // g.setPaint(paint);
            // g.fillRect(0, 0, width, height);

            // System.out.println("JConky.JConkyPainter.configureBackground(): " + g.getBackground());

            // g.setPaint(COLOR_FRAME_BACKGROUND);
            g.clearRect(0, 0, width, height);

            // g.setColor(COLOR_FRAME_BACKGROUND);
            // g.setBackground(COLOR_FRAME_BACKGROUND);
            // g.fillRect(0, 0, width, height);

            // super.configureBackground(g, width, height);
        }

        /**
         * @see de.freese.base.swing.components.graph.model.AbstractPainterModel#generateValue(int)
         */
        @Override
        protected void generateValue(final int width)
        {
            float value = (float) Math.random();

            addValue(value, width);
        }

        /**
         * @see de.freese.base.swing.components.graph.painter.AbstractGraphPainter#paintGraph(java.awt.Graphics2D, java.awt.Component, float, float)
         */
        @Override
        protected void paintGraph(final Graphics2D g, final Component parent, final float width, final float height)
        {
            // System.out.println("JConky.JConkyPainter.paintGraph(): " + g.getBackground());

            List<Float> values = getLastValues((int) width);

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
     *
     */
    private static final Color COLOR_FRAME_BACKGROUND = new Color(0, 0, 0, 75);

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

        DefaultGraphComponent graph = new DefaultGraphComponent(new JConkyPainter());
        // graph.setDoubleBuffered(true);
        // graph.setOpaque(true);
        // graph.setBackground(COLOR_FRAME_BACKGROUNF);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(graph::paintGraph, 500, 100, TimeUnit.MILLISECONDS);

        // jConky immer auf dem 2. Monitor.
        final GraphicsDevice graphicsDevice = gds[0];
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

        // Sonst kommt Exception: The frame is decorated
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("jConky", graphicsConfiguration);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(335, 1060);
        frame.setResizable(true);
        frame.setBackground(COLOR_FRAME_BACKGROUND);
        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);

        frame.add(graph);

        // Position releativ zum Monitor in der rechten oberen Ecke.
        frame.setLocation(graphicsConfiguration.getBounds().x, 10);
        // frame.setLocation(2700, 10);
        // frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
