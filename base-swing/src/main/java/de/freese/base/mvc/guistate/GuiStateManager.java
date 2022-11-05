package de.freese.base.mvc.guistate;

import java.awt.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.freese.base.swing.state.ButtonGuiState;
import de.freese.base.swing.state.ComboBoxGuiState;
import de.freese.base.swing.state.ContainerGuiState;
import de.freese.base.swing.state.FrameGuiState;
import de.freese.base.swing.state.GuiState;
import de.freese.base.swing.state.LabelGuiState;
import de.freese.base.swing.state.ListGuiState;
import de.freese.base.swing.state.StringGuiState;
import de.freese.base.swing.state.TabbedPaneGuiState;
import de.freese.base.swing.state.TableGuiState;
import de.freese.base.swing.state.TextComponentGuiState;
import de.freese.base.swing.state.TreeGuiState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Der {@link GuiStateManager} verwaltet die {@link GuiState}s.
 *
 * @author Thomas Freese
 */
public final class GuiStateManager
{
    private final Set<Class<? extends GuiState>> guiStates = new HashSet<>();

    private final Map<Class<? extends GuiState>, GuiState> instanceMap = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(GuiStateManager.class);

    private GuiStateProvider stateProvider;

    public GuiStateManager()
    {
        super();

        initDefaults();
    }

    public boolean addGUIState(final Class<? extends GuiState> stateClass)
    {
        if (!this.guiStates.contains(stateClass))
        {
            return this.guiStates.add(stateClass);
        }

        return false;
    }

    public Set<Class<? extends GuiState>> getGuiStates()
    {
        return Collections.unmodifiableSet(this.guiStates);
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public boolean removeGuiState(final Class<? extends GuiState> stateClass)
    {
        return this.guiStates.remove(stateClass);
    }

    @SuppressWarnings("unchecked")
    public void restore(final Component component, final String name)
    {
        GuiState stateTemplate = getState(component.getClass());

        GuiState state = getStateProvider().load(name, (Class<GuiState>) stateTemplate.getClass());

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.restore(component);
    }

    public void setStateProvider(final GuiStateProvider stateProvider)
    {
        this.stateProvider = stateProvider;
    }

    public void store(final Component component, final String name)
    {
        GuiState state = getState(component.getClass());

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.store(component);
        getStateProvider().save(name, state);
    }

    private synchronized GuiState getState(final Class<? extends Component> componentClass)
    {
        for (Class<? extends GuiState> stateClass : this.guiStates)
        {
            GuiState state = this.instanceMap.get(stateClass);

            if (state == null)
            {
                try
                {
                    state = stateClass.getDeclaredConstructor().newInstance();
                }
                catch (RuntimeException ex)
                {
                    throw ex;
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }

                this.instanceMap.put(stateClass, state);
            }

            if (state.supportsType(componentClass))
            {
                return state;
            }
        }

        return null;
    }

    private GuiStateProvider getStateProvider()
    {
        return this.stateProvider;
    }

    private void initDefaults()
    {
        addGUIState(ButtonGuiState.class);
        addGUIState(ComboBoxGuiState.class);
        addGUIState(ContainerGuiState.class);
        addGUIState(FrameGuiState.class);
        addGUIState(LabelGuiState.class);
        addGUIState(ListGuiState.class);
        addGUIState(StringGuiState.class);
        addGUIState(TabbedPaneGuiState.class);
        addGUIState(TableGuiState.class);
        addGUIState(TextComponentGuiState.class);
        addGUIState(TreeGuiState.class);
    }
}
