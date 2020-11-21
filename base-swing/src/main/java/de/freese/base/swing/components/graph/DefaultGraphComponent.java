// Created: 21.11.2020
package de.freese.base.swing.components.graph;

import javax.swing.Painter;
import de.freese.base.swing.components.graph.model.GraphModel;

/**
 * @author Thomas Freese
 */
public class DefaultGraphComponent extends AbstractGraphComponent
{
    /**
     *
     */
    private static final long serialVersionUID = -7419689107251752519L;

    /**
     * Erstellt ein neues {@link DefaultGraphComponent} Object.
     *
     * @param graphModel {@link GraphModel}
     * @param painter {@link Painter}
     */
    public DefaultGraphComponent(final GraphModel graphModel, final Painter<GraphModel> painter)
    {
        super(graphModel, painter);
    }
}
