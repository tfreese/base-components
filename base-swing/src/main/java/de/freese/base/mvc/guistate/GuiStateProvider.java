package de.freese.base.mvc.guistate;

import de.freese.base.swing.state.GuiState;

/**
 * Ein {@link GuiStateProvider} ist zuständig für das laden und speichern der {@link GuiState}s.
 *
 * @author Thomas Freese
 */
public interface GuiStateProvider
{
    /**
     * @param filePrefix String; ohne Extension
     */
    GuiState load(String filePrefix, Class<GuiState> stateClazz);

    /**
     * @param filePrefix String; ohne Extension
     */
    void save(String filePrefix, GuiState state);
}
