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
    void restore(Component component);

    void store(Component component);

    boolean supportsType(Class<?> type);
}
