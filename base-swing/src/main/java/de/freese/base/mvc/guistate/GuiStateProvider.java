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
     * Laden eines {@link GuiState}.
     *
     * @param filePrefix String; ohne Extension
     * @param stateClazz Class
     *
     * @return {@link GuiState}
     */
    GuiState load(String filePrefix, Class<GuiState> stateClazz);

    /**
     * Speichern eines {@link GuiState}.
     *
     * @param filePrefix String; ohne Extension
     * @param state {@link GuiState}
     */
    void save(String filePrefix, GuiState state);
}
