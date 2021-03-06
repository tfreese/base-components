package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import de.freese.base.swing.components.list.model.AbstractEventListListModel;
import de.freese.base.swing.eventlist.IEventList;

/**
 * Basis ComboBoxModel, welches die Verwendung einer {@link IEventList} ermoeglicht.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public abstract class AbstractEventListComboBoxModel<T> extends AbstractEventListListModel<T> implements ComboBoxModel<T>
{
    /**
     * Erweiterung des ComboboxEventListeners
     *
     * @author Thomas Freese
     */
    private class ComboBoxEventListListener extends EventListListener
    {
        /**
         * Ueberschrieben, um sicherzustellen, das das selektierte Objekt in der ComboBox angepasst wird, wenn sich die Daten der {I EventList} anpassen.
         *
         * @see de.freese.base.swing.components.list.model.AbstractEventListListModel.EventListListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        @Override
        public void contentsChanged(final ListDataEvent e)
        {
            AbstractEventListComboBoxModel.this.selectedObject = null;

            super.contentsChanged(e);
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -711561284816478818L;

    /**
     * Das momentan selektierte Objekt, in der ComboBox.
     */
    private Object selectedObject;

    /**
     * Erstellt ein neues {@link AbstractEventListComboBoxModel} Objekt.
     *
     * @param list {@link IEventList}
     */
    protected AbstractEventListComboBoxModel(final IEventList<T> list)
    {
        super(list);
    }

    /**
     * @see de.freese.base.swing.components.list.model.AbstractEventListListModel#createEventListener()
     */
    @Override
    protected EventListListener createEventListener()
    {
        return new ComboBoxEventListListener();
    }

    /**
     * Ueberschrieben, da beim Entfernen von Objekten auch das selektierte Objekt der ComboBox angepasst werden muss.
     *
     * @see de.freese.base.swing.components.list.model.AbstractEventListListModel#fireIntervalRemoved(java.lang.Object, int, int)
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
