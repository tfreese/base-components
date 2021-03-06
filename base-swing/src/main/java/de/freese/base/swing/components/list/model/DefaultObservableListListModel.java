// Created: 12.01.2018
package de.freese.base.swing.components.list.model;

import javax.swing.ListModel;

import javafx.collections.ObservableList;

/**
 * Defaultimplementierung eines {@link ListModel} fuer die {@link ObservableList}.
 *
 * @author Thomas Freese
 * @param <T> Typ der Entity
 */
public class DefaultObservableListListModel<T> extends AbstractObservableListListModel<T>
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultObservableListListModel}.
     *
     * @param list {@link ObservableList}
     */
    public DefaultObservableListListModel(final ObservableList<T> list)
    {
        super(list);
    }
}
