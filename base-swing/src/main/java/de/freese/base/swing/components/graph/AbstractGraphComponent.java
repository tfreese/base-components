// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphComponent extends Component
{
    /**
     *
     */
    private static final long serialVersionUID = -7006824316195250962L;

    /**
     *
     */
    private BufferedImage bufferedImage;

    /**
     *
     */
    private Graphics2D graphics2D;

    /**
     *
     */
    private final GraphModel graphModel;

    /**
     * Erstellt ein neues {@link AbstractGraphComponent} Object.
     */
    public AbstractGraphComponent()
    {
        super();

        this.graphModel = new GraphModel();

        addComponentListener(new ComponentAdapter()
        {
            /**
             * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentHidden(final ComponentEvent event)
            {
                onComponentHidden(event);
            }

            /**
             * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentResized(final ComponentEvent event)
            {
                onComponentResized(event);
            }

            /**
             * @see java.awt.event.ComponentAdapter#componentShown(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentShown(final ComponentEvent event)
            {
                onComponentShown(event);
            }
        });

        addMouseListener(new MouseAdapter()
        {
            /**
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(final MouseEvent event)
            {
                onMouseClicked(event);
            }
        });
    }

    /**
     * @return {@link BufferedImage}
     */
    protected BufferedImage getBufferedImage()
    {
        return this.bufferedImage;
    }

    /**
     * @return {@link Graphics2D}
     */
    protected Graphics2D getGraphics2D()
    {
        return this.graphics2D;
    }

    /**
     * @return {@link GraphModel}
     */
    protected GraphModel getGraphModel()
    {
        return this.graphModel;
    }

    /**
     * @param event {@link ComponentEvent}
     */
    protected void onComponentHidden(final ComponentEvent event)
    {
        // Empty
    }

    /**
     * @param event {@link ComponentEvent}
     */
    protected void onComponentResized(final ComponentEvent event)
    {
        setBufferedImage((BufferedImage) createImage(getWidth(), getHeight()));

        getGraphModel().setNewSize(getWidth());
    }

    /**
     * @param event {@link ComponentEvent}
     */
    protected void onComponentShown(final ComponentEvent event)
    {
        // Empty
    }

    /**
     * @param event {@link MouseEvent}
     */
    protected void onMouseClicked(final MouseEvent event)
    {
        // Empty
    }

    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        if (getBufferedImage() == null)
        {
            return;
        }

        g.drawImage(getBufferedImage(), 0, 0, this);
        // g.drawImage(getBufferedImage(), 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * @param g {@link Graphics2D}
     */
    protected void paintGraph(final Graphics2D g)
    {
        if ((g == null) || (getGraphModel().size() < 2))
        {
            return;
        }

        // Koordinatenursprung ist nach unten links verlegt.
        // siehe #setBufferedImage

        g.setBackground(getBackground());
        g.clearRect(0, 0, getWidth(), getHeight());

        int yStart = 0;
        int xStart = getWidth() - getGraphModel().size() - 1; // Diagramm von rechts aufbauen.
        // int xStart = 0; // Diagramm von links aufbauen.
        int graphHeight = getHeight();

        g.setColor(Color.GREEN);
        g.drawRect(0, 1, getWidth() - 1, graphHeight - 1);

        g.setColor(Color.YELLOW);

        float yValueLast = getGraphModel().getYKoordinate(0, graphHeight);

        for (int i = 1; i < getGraphModel().size(); i++)
        {
            // float yValue = yStart + (graphHeight * getValues().get(i));

            float yValue = getGraphModel().getYKoordinate(i, graphHeight);

            g.drawLine((xStart + i) - 1, (int) yValueLast, xStart + i, (int) yValue);

            yValueLast = yValue;
        }

        repaint();
    }

    /**
     * @param bufferedImage {@link BufferedImage}
     */
    protected void setBufferedImage(final BufferedImage bufferedImage)
    {
        this.bufferedImage = bufferedImage;

        this.graphics2D = this.bufferedImage.createGraphics();

        // Koordinatenursprung von oben links nach unten links verlegen.
        this.graphics2D.scale(1.0D, -1.0D); // Kippt die Y-Achse nach oben.
        this.graphics2D.translate(0, -getHeight()); // Verschiebt die 0-0 Koordinate nach unten.
    }

    /**
     * @param value float
     */
    public void setValue(final float value)
    {
        getGraphModel().addValue(value, getWidth());

        if (SwingUtilities.isEventDispatchThread())
        {
            paintGraph(getGraphics2D());
        }
        else
        {
            SwingUtilities.invokeLater(() -> paintGraph(getGraphics2D()));
        }
    }
}
