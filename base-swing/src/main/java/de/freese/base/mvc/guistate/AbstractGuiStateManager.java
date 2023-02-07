package de.freese.base.mvc.guistate;

import java.awt.Component;
import java.util.Objects;

import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.GuiStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGuiStateManager implements GuiStateManager
{
    private final GuiStates guiStates;

    private final LocalStorage localStorage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractGuiStateManager(final LocalStorage localStorage, final GuiStates guiStates)
    {
        super();

        this.localStorage = Objects.requireNonNull(localStorage, "localStorage required");
        this.guiStates = Objects.requireNonNull(guiStates, "guiStates required");
    }

    @Override
    public void restore(final Component component, final String name)
    {
        GuiState stateTemplate = getGuiStates().getState(component.getClass());

        GuiState state = load((Class<GuiState>) stateTemplate.getClass(), name);

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.restore(component);
    }

    @Override
    public void store(final Component component, final String name)
    {
        GuiState state = getGuiStates().getState(component.getClass());

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.store(component);

        save(state, name);
    }

    protected GuiStates getGuiStates()
    {
        return this.guiStates;
    }

    protected LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    protected abstract GuiState load(Class<GuiState> stateClazz, String name);

    protected abstract void save(GuiState state, String name);
}
