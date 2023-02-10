package de.freese.base.mvc.guistate;

import java.awt.Component;

import de.freese.base.swing.state.GuiState;

/**
 * {@link GuiStateManager}  loads and saves the {@link GuiState}s for {@link Component}s.
 *
 * @author Thomas Freese
 */
public interface GuiStateManager {
    void restore(Component component, String name);

    void store(Component component, String name);
}
