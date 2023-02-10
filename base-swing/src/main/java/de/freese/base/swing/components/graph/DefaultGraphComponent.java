// Created: 21.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import java.io.Serial;

import javax.swing.Painter;

/**
 * @author Thomas Freese
 */
public class DefaultGraphComponent extends AbstractGraphComponent {
    @Serial
    private static final long serialVersionUID = -7419689107251752519L;

    public DefaultGraphComponent(final Painter<Component> painter) {
        super(painter);
    }
}
