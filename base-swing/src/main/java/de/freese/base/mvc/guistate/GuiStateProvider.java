package de.freese.base.mvc.guistate;

import de.freese.base.swing.state.GuiState;

/**
 * {@link GuiStateProvider}  loads and saves the {@link GuiState}s.
 *
 * @author Thomas Freese
 */
public interface GuiStateProvider
{
    /**
     * @param filePrefix String; without Extension
     */
    GuiState load(String filePrefix, Class<GuiState> stateClazz);

    /**
     * @param filePrefix String; without Extension
     */
    void save(String filePrefix, GuiState state);
}
