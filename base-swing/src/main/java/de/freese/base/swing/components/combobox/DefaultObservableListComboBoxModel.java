// Created: 12.01.2018
package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;

import javafx.collections.ObservableList;

/**
 * Defaultimplementierung eines {@link ComboBoxModel} für die {@link ObservableList}.
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T>
{
    public DefaultObservableListComboBoxModel(final ObservableList<T> list)
    {
        super(list);
    }
}
