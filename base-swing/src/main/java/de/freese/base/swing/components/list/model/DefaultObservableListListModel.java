// Created: 12.01.2018
package de.freese.base.swing.components.list.model;

import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public class DefaultObservableListListModel<T> extends AbstractObservableListListModel<T>
{
    public DefaultObservableListListModel(final ObservableList<T> list)
    {
        super(list);
    }
}
