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
import de.freese.base.swing.state.GUIState;
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
 * Der {@link GuiStateManager} verwaltet die {@link GUIState}s.
 *
 * @author Thomas Freese
 */
public final class GuiStateManager
{
    /**
     *
     */
    private final Set<Class<? extends GUIState>> guiStates = new HashSet<>();
    /**
     *
     */
    private final Map<Class<? extends GUIState>, GUIState> instanceMap = new HashMap<>();
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(GuiStateManager.class);
    /**
     *
     */
    private GuiStateProvider stateProvider;

    /**
     * Erstellt ein neues {@link GuiStateManager} Object.
     */
    public GuiStateManager()
    {
        super();

        initDefaults();
    }

    /**
     * Hinzufuegen eines neuen {@link GUIState}s.
     *
     * @param stateClass {@link Class}
     *
     * @return boolean
     */
    public boolean addGUIState(final Class<? extends GUIState> stateClass)
    {
        if (!this.guiStates.contains(stateClass))
        {
            return this.guiStates.add(stateClass);
        }

        return false;
    }

    /**
     * Liefert alle {@link GUIState}.
     *
     * @return {@link Set}
     */
    public Set<Class<? extends GUIState>> getGuiStates()
    {
        return Collections.unmodifiableSet(this.guiStates);
    }

    /**
     * @return {@link Logger}
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Entfernt den {@link GUIState}.
     *
     * @param stateClass {@link Class}
     *
     * @return boolean
     */
    public boolean removeGuiState(final Class<? extends GUIState> stateClass)
    {
        return this.guiStates.remove(stateClass);
    }

    /**
     * Wiederherstellen einer {@link Component} aus einem {@link GUIState}.
     *
     * @param component {@link Component}
     * @param name String
     */
    @SuppressWarnings("unchecked")
    public void restore(final Component component, final String name)
    {
        GUIState stateTemplate = getState(component.getClass());

        GUIState state = getStateProvider().load(name, (Class<GUIState>) stateTemplate.getClass());

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.restore(component);
    }

    /**
     * @param stateProvider {@link GuiStateProvider}
     */
    public void setStateProvider(final GuiStateProvider stateProvider)
    {
        this.stateProvider = stateProvider;
    }

    /**
     * Speichern eines {@link GUIState}s fuer eine {@link Component}.
     *
     * @param component {@link Component}
     * @param name String
     */
    public void store(final Component component, final String name)
    {
        GUIState state = getState(component.getClass());

        if (state == null)
        {
            // LOGGER.warn("GuiState not found for: {}", component.getClass());

            return;
        }

        state.store(component);
        getStateProvider().save(name, state);
    }

    /**
     * Liefert den GuiState fuer eine {@link Component}.
     *
     * @param componentClass Class
     *
     * @return {@link GUIState}
     */
    private synchronized GUIState getState(final Class<? extends Component> componentClass)
    {
        for (Class<? extends GUIState> stateClass : this.guiStates)
        {
            GUIState state = this.instanceMap.get(stateClass);

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

    /**
     * @return {@link GuiStateProvider}
     */
    private GuiStateProvider getStateProvider()
    {
        return this.stateProvider;
    }

    /**
     * Registrieren von DefaultGuiStates etc.
     */
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
