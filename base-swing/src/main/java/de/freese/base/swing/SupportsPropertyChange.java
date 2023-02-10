package de.freese.base.swing;

import java.beans.PropertyChangeSupport;

/**
 * Interface für ein Objekt, welches ein {@link PropertyChangeSupport} unterstützt.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SupportsPropertyChange {
    PropertyChangeSupport getPropertyChangeSupport();
}
