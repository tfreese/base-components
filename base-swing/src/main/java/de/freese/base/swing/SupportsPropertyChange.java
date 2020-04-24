package de.freese.base.swing;

import java.beans.PropertyChangeSupport;

/**
 * Interface fuer ein Objekt, welches ein {@link PropertyChangeSupport} unterstuetzt.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SupportsPropertyChange
{
    /**
     * Liefert {@link PropertyChangeSupport} der Implementierung.
     *
     * @return {@link PropertyChangeSupport}
     */
    public PropertyChangeSupport getPropertyChangeSupport();
}
