// Created: 12.01.2018
package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;
import de.freese.base.swing.components.list.model.AbstractObservableListListModel;
import javafx.collections.ObservableList;

/**
 * Basis ComboBoxModel, welches die Verwendung einer {@link ObservableList} ermoeglicht.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public abstract class AbstractObservableListComboBoxModel<T> extends AbstractObservableListListModel<T> implements ComboBoxModel<T>
{
    /**
     * Das momentan selektierte Objekt, in der ComboBox.
     */
    private Object selectedObject;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractObservableListComboBoxModel}.
     *
     * @param list {@link ObservableList}
     */
    protected AbstractObservableListComboBoxModel(final ObservableList<T> list)
    {
        super(list);
    }

    /**
     * @see de.freese.base.swing.components.list.model.AbstractObservableListListModel#fireContentsChanged(java.lang.Object, int, int)
     */
    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1)
    {
        this.selectedObject = null;

        super.fireContentsChanged(source, index0, index1);
    }

    /**
     * @see de.freese.base.swing.components.list.model.AbstractObservableListListModel#fireIntervalRemoved(java.lang.Object, int, int)
     */
    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1)
    {
        if (this.selectedObject != null)
        {
            setSelectedItem(null);
        }

        super.fireIntervalRemoved(source, index0, index1);
    }

    /**
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    @Override
    public Object getSelectedItem()
    {
        return this.selectedObject;
    }

    /**
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    @Override
    public void setSelectedItem(final Object anItem)
    {
        int index = getList().indexOf(anItem);

        if (index != -1)
        {
            this.selectedObject = getList().get(index);
        }
        else
        {
            this.selectedObject = null;
        }

        fireContentsChanged(this, index, index);
    }
}
