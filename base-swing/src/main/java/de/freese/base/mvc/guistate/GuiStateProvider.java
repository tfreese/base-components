package de.freese.base.mvc.guistate;

import de.freese.base.swing.state.GUIState;

/**
 * Ein {@link GuiStateProvider} ist zuständig für das laden und speichern der {@link GUIState}s.
 *
 * @author Thomas Freese
 */
public interface GuiStateProvider
{
    /**
     * Laden eines {@link GUIState}.
     *
     * @param filePrefix String; ohne Extension
     * @param stateClazz Class
     *
     * @return {@link GUIState}
     */
    GUIState load(String filePrefix, Class<GUIState> stateClazz);

    /**
     * Speichern eines {@link GUIState}.
     *
     * @param filePrefix String; ohne Extension
     * @param state {@link GUIState}
     */
    void save(String filePrefix, GUIState state);
}
