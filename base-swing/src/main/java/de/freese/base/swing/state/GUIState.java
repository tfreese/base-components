package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serializable;

/**
 * Interface um Zustaende von {@link Component}s zu speichern und wieder herzustellen.
 *
 * @author Thomas Freese
 */
public interface GUIState extends Serializable
{
    /**
     * Stellt die {@link Component} aus dem {@link GUIState} wieder her.
     * 
     * @param component {@link Component}
     */
    public void restore(Component component);

    /**
     * Speichern den Zustand der {@link Component} in dem {@link GUIState}
     * 
     * @param component {@link Component}
     */
    public void store(Component component);

    /**
     * Liefert true wenn der {@link GUIState} diesen Typ unterstuetzt.
     * 
     * @param type Class
     * @return boolean
     */
    public boolean supportsType(final Class<?> type);
}
