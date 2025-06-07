// Created: 12.01.2018
package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;

import javafx.collections.ObservableList;

import de.freese.base.swing.components.list.model.AbstractObservableListListModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractObservableListComboBoxModel<T> extends AbstractObservableListListModel<T> implements ComboBoxModel<T> {
    private Object selectedObject;

    protected AbstractObservableListComboBoxModel(final ObservableList<T> list) {
        super(list);
    }

    @Override
    public Object getSelectedItem() {
        return selectedObject;
    }

    @Override
    public void setSelectedItem(final Object anItem) {
        final int index = getList().indexOf(anItem);

        if (index != -1) {
            selectedObject = getList().get(index);
        }
        else {
            selectedObject = null;
        }

        fireContentsChanged(this, index, index);
    }

    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        selectedObject = null;

        super.fireContentsChanged(source, index0, index1);
    }

    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        if (selectedObject != null) {
            setSelectedItem(null);
        }

        super.fireIntervalRemoved(source, index0, index1);
    }
}
