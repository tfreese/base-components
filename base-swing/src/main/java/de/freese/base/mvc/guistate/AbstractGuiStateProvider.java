package de.freese.base.mvc.guistate;

import java.util.Objects;
import java.util.Set;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Der {@link AbstractGuiStateProvider} nutzt den {@link LocalStorage} für das Speichern im verschiedenen Formaten.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGuiStateProvider implements GuiStateProvider
{
    private final Class<?>[] guiStateClasses;

    private final LocalStorage localStorage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractGuiStateProvider(final LocalStorage localStorage, final GuiStateManager guiStateManager)
    {
        super();

        this.localStorage = Objects.requireNonNull(localStorage, "localStorage required");
        Objects.requireNonNull(guiStateManager, "guiStateManager required");

        Set<Class<? extends GuiState>> guiStates = guiStateManager.getGuiStates();
        this.guiStateClasses = new Class<?>[guiStates.size()];
        int i = 0;

        for (Class<? extends GuiState> guiStateClazz : guiStates)
        {
            this.guiStateClasses[i] = guiStateClazz;
            i++;
        }
    }

    protected Class<?>[] getGuiStateClasses()
    {
        return this.guiStateClasses;
    }

    protected LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }
}
