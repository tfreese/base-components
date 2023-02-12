// Created: 07.02.23
package de.freese.base.swing.state;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public final class GuiStates {
    public static GuiStates ofDefaults() {
        GuiStates guiStates = new GuiStates();
        guiStates.customize(GuiStates::defaultConverters);

        return guiStates;
    }

    private static void defaultConverters(Set<Class<? extends GuiState>> guiStates) {
        guiStates.add(ButtonGuiState.class);
        guiStates.add(ComboBoxGuiState.class);
        guiStates.add(ContainerGuiState.class);
        guiStates.add(FrameGuiState.class);
        guiStates.add(LabelGuiState.class);
        guiStates.add(ListGuiState.class);
        guiStates.add(StringGuiState.class);
        guiStates.add(TabbedPaneGuiState.class);
        guiStates.add(TableGuiState.class);
        guiStates.add(TextComponentGuiState.class);
        guiStates.add(TreeGuiState.class);
    }

    private final Map<Class<? extends GuiState>, GuiState> instanceMap = new HashMap<>();
    
    private final Set<Class<? extends GuiState>> states = new HashSet<>();

    private GuiStates() {
        super();
    }

    public void customize(Consumer<Set<Class<? extends GuiState>>> guiStatesCustomizer) {
        guiStatesCustomizer.accept(states);
    }

    public Class<GuiState>[] getGuiStates() {
        return states.toArray(Class[]::new);
    }

    public GuiState getState(final Class<? extends Component> componentClass) {
        for (Class<? extends GuiState> stateClass : states) {
            GuiState state = this.instanceMap.get(stateClass);

            if (state == null) {
                try {
                    state = stateClass.getDeclaredConstructor().newInstance();
                }
                catch (RuntimeException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                this.instanceMap.put(stateClass, state);
            }

            if (state.supportsType(componentClass)) {
                return state;
            }
        }

        return null;
    }
}
