// Created: 21.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import javax.swing.Painter;

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
     * @param painter {@link Painter}
     */
    public DefaultGraphComponent(final Painter<Component> painter)
    {
        super(painter);
    }
}
