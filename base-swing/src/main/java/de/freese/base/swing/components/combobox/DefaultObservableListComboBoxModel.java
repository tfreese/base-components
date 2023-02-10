// Created: 12.01.2018
package de.freese.base.swing.components.combobox;

import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T> {
    public DefaultObservableListComboBoxModel(final ObservableList<T> list) {
        super(list);
    }
}
