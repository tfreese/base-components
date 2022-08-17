// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.Objects;

import javax.swing.Painter;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphComponent extends Component
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -7006824316195250962L;
    /**
     *
     */
    private final transient Painter<Component> painter;
    /**
     *
     */
    private transient BufferedImage bufferedImage;
    /**
     *
     */
    private transient Graphics2D bufferedImageGraphics2d;
    /**
     *
     */
    private boolean useBufferedImage;

    /**
     * Erstellt ein neues {@link AbstractGraphComponent} Object.
     *
     * @param painter {@link Painter}
     */
    protected AbstractGraphComponent(final Painter<Component> painter)
    {
        super();

        this.painter = Objects.requireNonNull(painter, "painter required");

        init();
    }

    /**
     * @return boolean
     */
    public boolean isUseBufferedImage()
    {
        return this.useBufferedImage;
    }

    /**
     * Nur verwenden, wenn Klasse von Component vererbt !!!
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        // super.paint(g);

        if (isUseBufferedImage() && (getBufferedImage() != null))
        {
            getPainter().paint(getBufferedImageGraphics2d(), this, getWidth(), getHeight());

            g.drawImage(getBufferedImage(), 0, 0, this);
        }
        else
        {
            Graphics2D g2d = (Graphics2D) g;

            getPainter().paint(g2d, this, getWidth(), getHeight());
        }
    }

    /**
     * Führt den Repaint immer im EDT aus.
     */
    public void paintGraph()
    {
        // ((AbstractGraphPainter) getPainter()).generateValue(getWidth());

        if (SwingUtilities.isEventDispatchThread())
        {
            repaint();
        }
        else
        {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    /**
     * @param useBufferedImage boolean
     */
    public void useBufferedImage(final boolean useBufferedImage)
    {
        this.useBufferedImage = useBufferedImage;
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
    protected Graphics2D getBufferedImageGraphics2d()
    {
        return this.bufferedImageGraphics2d;
    }

    /**
     * @return {@link Painter}<Component>
     */
    protected Painter<Component> getPainter()
    {
        return this.painter;
    }

    /**
     *
     */
    protected void init()
    {
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

        addComponentListener(new ComponentAdapter()
        {
            /**
             * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentResized(final ComponentEvent event)
            {
                onComponentResized(event);
            }
        });
    }

    /**
     * @param event {@link ComponentEvent}
     */
    protected void onComponentResized(final ComponentEvent event)
    {
        // this.bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.bufferedImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        // this.bufferedImage = (BufferedImage) createImage(getWidth(), getHeight());

        this.bufferedImageGraphics2d = this.bufferedImage.createGraphics();
    }

    // /**
    // * Nur verwenden wenn Klasse von JComponent vererbt !!!
    // *
    // * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    // */
    // @Override
    // protected void paintComponent(final Graphics g)
    // {
    // // super.paintComponent(g);
    //
    // if (this.bufferedImage != null)
    // {
    // getPainter().paint(this.bufferedImageGraphics2d, this, getWidth(), getHeight());
    //
    // g.drawImage(this.bufferedImage, 0, 0, this);
    // }
    // else
    // {
    // Graphics2D g2d = (Graphics2D) g;
    //
    // getPainter().paint(g2d, this, getWidth(), getHeight());
    // }
    // }

    /**
     * @param event {@link MouseEvent}
     */
    protected void onMouseClicked(final MouseEvent event)
    {
        // Empty
    }
}
