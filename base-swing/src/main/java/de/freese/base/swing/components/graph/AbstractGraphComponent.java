// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private static final long serialVersionUID = -7006824316195250962L;

    // /**
    // *
    // */
    // private BufferedImage bufferedImage;
    //
    // /**
    // *
    // */
    // private Graphics2D bufferedImageGraphic2d;

    /**
     *
     */
    private final transient Painter<Component> painter;

    /**
     * Erstellt ein neues {@link AbstractGraphComponent} Object.
     *
     * @param painter {@link Painter}
     */
    public AbstractGraphComponent(final Painter<Component> painter)
    {
        super();

        this.painter = Objects.requireNonNull(painter, "painter required");

        init();
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
    }

    /**
     * @param event {@link MouseEvent}
     */
    protected void onMouseClicked(final MouseEvent event)
    {
        // Empty
    }

    /**
     * Nur verwenden wenn Klasse von Component vererbt !!!
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        // super.paint(g);

        // if (this.bufferedImage == null)
        // {
        // // this.bufferedImage = (BufferedImage) createImage(getWidth(), getHeight());
        // // this.bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        // this.bufferedImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        // this.bufferedImageGraphic2d = this.bufferedImage.createGraphics();
        // }
        //
        // getPainter().paint(this.bufferedImageGraphic2d, this, getWidth(), getHeight());
        // g.drawImage(this.bufferedImage, 0, 0, this);

        Graphics2D g2d = (Graphics2D) g;

        getPainter().paint(g2d, this, getWidth(), getHeight());
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
    // System.out.println("AbstractGraphComponent.paintComponent(): " + getBackground());
    //
    // // g.drawImage(getBufferedImage(), 0, 0, this);
    //
    // Graphics2D g2d = (Graphics2D) g;
    //
    // getPainter().paint(g2d, this, getWidth(), getHeight());
    // }

    /**
     * Führt den Repaint immer im EDT aus.
     */
    public void paintGraph()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            repaint();
        }
        else
        {
            SwingUtilities.invokeLater(this::repaint);
        }
    }
}
