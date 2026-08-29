package de.freese.base.swing.components.list.model;

import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 * @since 12.01.2018
 */
public class DefaultObservableListListModel<T> extends AbstractObservableListListModel<T> {
    public DefaultObservableListListModel(final ObservableList<T> list) {
        super(list);
    }
}
