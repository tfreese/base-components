package de.freese.base.swing.components.combobox;

import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 * @since 12.01.2018
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T> {
    public DefaultObservableListComboBoxModel(final ObservableList<T> list) {
        super(list);
    }
}
