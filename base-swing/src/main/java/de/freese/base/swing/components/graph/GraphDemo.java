// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import de.freese.base.swing.components.graph.painter.AbstractGraphPainter;
import de.freese.base.swing.components.graph.painter.LineGraphPainter;

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

        GraphDemo graphDemo = new GraphDemo(new LineGraphPainter(), Executors.newScheduledThreadPool(1));
        graphDemo.setBackground(Color.BLACK);
        graphDemo.start();

        // frame.setUndecorated(true);
        // frame.setOpacity(0.55F);
        frame.setResizable(true);
        frame.getContentPane().add(graphDemo);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     *
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     *
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * Erstellt ein neues {@link GraphDemo} Object.
     *
     * @param painter {@link AbstractGraphPainter}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     */
    private GraphDemo(final AbstractGraphPainter painter, final ScheduledExecutorService scheduledExecutorService)
    {
        super(painter);

        this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService required");
    }

    /**
     * @see de.freese.base.swing.components.graph.AbstractGraphComponent#onMouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    protected void onMouseClicked(final MouseEvent event)
    {
        if (((event.getModifiersEx()) & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)
        {
            System.gc();
        }
        else if (this.scheduledFuture == null)
        {
            start();
        }
        else
        {
            stop();
        }
    }

    /**
     *
     */
    private void start()
    {
        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            setValue((float) Math.random());
            // setValue((float) Runtime.getRuntime().freeMemory() / (float) Runtime.getRuntime().totalMemory());
        }, 100, 40, TimeUnit.MILLISECONDS);
    }

    /**
     *
     */
    private void stop()
    {
        if ((this.scheduledFuture != null))
        {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }
    }
}
