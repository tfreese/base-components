// Created: 29.07.2021
package de.freese.base.swing.components.combobox;

import java.io.Serial;

import javax.swing.ComboBoxModel;

import de.freese.base.swing.components.list.model.DefaultListListModel;

/**
 * @author Thomas Freese
 */
public class DefaultListComboBoxModel<T> extends DefaultListListModel<T> implements ComboBoxModel<T> {
    @Serial
    private static final long serialVersionUID = 8956293128753923538L;

    private transient Object selectedObject;

    @Override
    public Object getSelectedItem() {
        return this.selectedObject;
    }

    @Override
    public void setSelectedItem(final Object anItem) {
        int index = getList().indexOf(anItem);

        if (index != -1) {
            this.selectedObject = getList().get(index);
        }
        else {
            this.selectedObject = null;
        }

        fireContentsChanged(this, index, index);
    }

    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {
        this.selectedObject = null;

        super.fireContentsChanged(source, index0, index1);
    }

    /**
     * Überschrieben, da beim Entfernen von Objekten auch das selektierte Objekt der ComboBox angepasst werden muss.
     */
    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
        if (this.selectedObject != null) {
            setSelectedItem(null);
        }

        super.fireIntervalRemoved(source, index0, index1);
    }
}
