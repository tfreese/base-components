// Created: 12.01.2018
package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;

import javafx.collections.ObservableList;

/**
 * Defaultimplementierung eines {@link ComboBoxModel} fuer die {@link ObservableList}.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class DefaultObservableListComboBoxModel<T> extends AbstractObservableListComboBoxModel<T>
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultObservableListComboBoxModel}.
     *
     * @param list {@link ObservableList}
     */
    public DefaultObservableListComboBoxModel(final ObservableList<T> list)
    {
        super(list);
    }
}
