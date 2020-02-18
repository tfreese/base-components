package de.freese.base.mvc.context.guistate;

import de.freese.base.swing.state.GUIState;

/**
 * Ein {@link GuiStateProvider} ist zustaendig fuer das laden und speichern der {@link GUIState}s.
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
     * @return {@link GUIState}
     */
    public GUIState load(String filePrefix, Class<GUIState> stateClazz);

    /**
     * Speichern eines {@link GUIState}.
     *
     * @param filePrefix String; ohne Extension
     * @param state {@link GUIState}
     */
    public void save(String filePrefix, GUIState state);
}
