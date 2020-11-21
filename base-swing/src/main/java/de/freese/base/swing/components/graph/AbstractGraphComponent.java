// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.Painter;
import javax.swing.SwingUtilities;
import de.freese.base.swing.components.graph.model.AbstractGraphModel;
import de.freese.base.swing.components.graph.model.GraphModel;
import de.freese.base.swing.components.graph.painter.AbstractGraphPainter;

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
    private final GraphModel graphModel;

    /**
     *
     */
    private final Painter<GraphModel> painter;

    /**
     * Erstellt ein neues {@link AbstractGraphComponent} Object.
     *
     * @param graphModel {@link GraphModel}
     * @param painter {@link AbstractGraphPainter}
     */
    public AbstractGraphComponent(final GraphModel graphModel, final Painter<GraphModel> painter)
    {
        super();

        this.graphModel = Objects.requireNonNull(graphModel, "graphModel required");
        this.painter = Objects.requireNonNull(painter, "painter required");

        init();
    }

    /**
     * @return {@link AbstractGraphModel}
     */
    protected GraphModel getGraphModel()
    {
        return this.graphModel;
    }

    /**
     * @return {@link Painter}<GraphModel>
     */
    protected Painter<GraphModel> getPainter()
    {
        return this.painter;
    }

    /**
     *
     */
    protected void init()
    {
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
        getGraphModel().setSize(getWidth());
    }

    /**
     * @param event {@link ComponentEvent}
     */
    protected void onComponentShown(final ComponentEvent event)
    {
        getGraphModel().setSize(getWidth());
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

        getPainter().paint(g2d, getGraphModel(), getWidth(), getHeight());
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
