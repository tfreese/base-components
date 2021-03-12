package de.freese.base.mvc.guistate;

import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GUIState;

/**
 * Der {@link AbstractGuiStateProvider} nutzt den {@link LocalStorage} fuer das Speichern im verschiedenen Formaten.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGuiStateProvider implements GuiStateProvider
{
    /**
    *
    */
    private final Class<?>[] guiStateClasses;

    /**
     *
     */
    private final LocalStorage localStorage;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractGuiStateProvider} Object.
     *
     * @param localStorage {@link LocalStorage}
     * @param guiStateManager {@link GuiStateManager}
     */
    protected AbstractGuiStateProvider(final LocalStorage localStorage, final GuiStateManager guiStateManager)
    {
        super();

        this.localStorage = Objects.requireNonNull(localStorage, "localStorage required");
        Objects.requireNonNull(guiStateManager, "guiStateManager required");

        Set<Class<? extends GUIState>> guiStates = guiStateManager.getGuiStates();
        this.guiStateClasses = new Class<?>[guiStates.size()];
        int i = 0;

        for (Class<? extends GUIState> guiStateClazz : guiStates)
        {
            this.guiStateClasses[i] = guiStateClazz;
            i++;
        }
    }

    /**
     * @return Class<?>[]
     */
    protected Class<?>[] getGuiStateClasses()
    {
        return this.guiStateClasses;
    }

    /**
     * @return {@link LocalStorage}
     */
    protected LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
