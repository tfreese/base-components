package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serializable;

/**
 * Interface um Zustände von {@link Component}s zu speichern und wieder herzustellen.
 *
 * @author Thomas Freese
 */
public interface GuiState extends Serializable
{
    /**
     * Stellt die {@link Component} aus dem {@link GuiState} wieder her.
     *
     * @param component {@link Component}
     */
    void restore(Component component);

    /**
     * Speichern den Zustand der {@link Component} in dem {@link GuiState}
     *
     * @param component {@link Component}
     */
    void store(Component component);

    /**
     * Liefert true wenn der {@link GuiState} diesen Typ unterstützt.
     *
     * @param type Class
     *
     * @return boolean
     */
    boolean supportsType(final Class<?> type);
}
