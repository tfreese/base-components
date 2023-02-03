package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serializable;

/**
 * Interface for saving and restoring states of {@link Component}s.
 *
 * @author Thomas Freese
 */
public interface GuiState extends Serializable
{
    void restore(Component component);

    void store(Component component);

    boolean supportsType(Class<?> type);
}
