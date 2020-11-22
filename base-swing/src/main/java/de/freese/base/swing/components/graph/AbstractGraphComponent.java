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
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        // g.drawImage(getBufferedImage(), 0, 0, this);

        Graphics2D g2d = (Graphics2D) g;

        getPainter().paint(g2d, this, getWidth(), getHeight());
    }

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
