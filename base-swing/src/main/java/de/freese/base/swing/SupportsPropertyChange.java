package de.freese.base.swing;

import java.beans.PropertyChangeSupport;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SupportsPropertyChange {
    PropertyChangeSupport getPropertyChangeSupport();
}
