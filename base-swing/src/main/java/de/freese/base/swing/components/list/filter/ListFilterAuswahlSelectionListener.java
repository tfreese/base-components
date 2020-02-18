package de.freese.base.swing.components.list.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import de.freese.base.core.model.IdentifierProvider;
import de.freese.base.swing.components.list.filter.callback.EmptyListFilterAuswahlCallback;
import de.freese.base.swing.components.list.model.AbstractEventListListModel;
import de.freese.base.swing.eventlist.FilterableEventList;
import de.freese.base.swing.filter.AndFilter;
import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.filter.FilterCondition;

/**
 * Verknuepft zwei {@link JList} miteinander.<br>
 * Die Child-List reagiert auf Selektionen der Parent-List.<br>
 * Beide Listen muessen ein {@link ListModel} vom Typ {@link AbstractEventListListModel} besitzen, indem eine {@link FilterableEventList} vorhanden ist.<br>
 * Saemtliche Objekte innerhalb der Liste muessen das Interface {@link IdentifierProvider} implementieren.<br>
 * <br>
 * Diese Klasse sollte verwendet werden NACHDEM die Listen fertig konfiguriert sind (EventListModel, Filter etc.)
 *
 * @author Thomas Freese
 * @see IListFilterAuswahlCallback
 */
public class ListFilterAuswahlSelectionListener implements ListSelectionListener, FilterCondition, PropertyChangeListener
{
    /**
     *
     */
    private IListFilterAuswahlCallback callback = null;

    /**
     * Liste des Childs.
     */
    private final FilterableEventList<?> childFilterableEventList;

    /**
     *
     */
    private Set<Long> childIds = null;

    /**
     *
     */
    private final JList<?> childList;

    /**
     *
     */
    private final JList<?> parentList;

    /**
     *
     */
    private transient PropertyChangeSupport propertyChangeSupport = null;

    /**
     * Erstellt ein neues {@link ListFilterAuswahlSelectionListener} Objekt.
     *
     * @param parent {@link JList}
     * @param child {@link JList}
     */
    @SuppressWarnings("rawtypes")
    public ListFilterAuswahlSelectionListener(final JList parent, final JList child)
    {
        super();

        this.parentList = parent;
        this.childList = child;

        // Parent
        this.parentList.addListSelectionListener(this);
        AbstractEventListListModel listModel = (AbstractEventListListModel) parent.getModel();
        FilterableEventList parentFilterableEventList = (FilterableEventList) listModel.getList();
        FilterCondition filter = parentFilterableEventList.getFilter();
        filter.getPropertyChangeSupport().addPropertyChangeListener(this);

        // Child
        listModel = (AbstractEventListListModel) child.getModel();
        this.childFilterableEventList = (FilterableEventList) listModel.getList();

        // Den gesetzten Filter ersetzen.
        filter = this.childFilterableEventList.getFilter();

        AndFilter andFilter = new AndFilter();
        andFilter.addFilter(filter);
        andFilter.addFilter(this);

        this.childFilterableEventList.setListenerEnabled(false);
        this.childFilterableEventList.setFilter(andFilter);
        this.childFilterableEventList.setListenerEnabled(true);
    }

    /**
     * @return {@link IListFilterAuswahlCallback}
     */
    private IListFilterAuswahlCallback getCallback()
    {
        return this.callback != null ? this.callback : EmptyListFilterAuswahlCallback.getInstance();
    }

    /**
     * @return {@link Set}
     */
    private Set<Long> getChildIds()
    {
        Set<Long> emptySet = Collections.emptySet();

        return this.childIds != null ? this.childIds : emptySet;
    }

    /**
     * @see de.freese.base.core.model.SupportsPropertyChange#getPropertyChangeSupport()
     */
    @Override
    public PropertyChangeSupport getPropertyChangeSupport()
    {
        if (this.propertyChangeSupport == null)
        {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        return this.propertyChangeSupport;
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        if (Filter.FILTER_CHANGED.equals(evt.getPropertyName()))
        {
            this.parentList.clearSelection();

            try
            {
                this.parentList.setSelectedIndex(0);
            }
            catch (Exception ex)
            {
                // NOOP
            }
        }
    }

    /**
     * @param callback {@link IListFilterAuswahlCallback}
     */
    public void setCallback(final IListFilterAuswahlCallback callback)
    {
        this.callback = callback;
    }

    /**
     * @see de.freese.base.swing.filter.FilterCondition#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        // Filter die Childobjekte.
        if (object instanceof IdentifierProvider)
        {
            IdentifierProvider identifierProvider = (IdentifierProvider) object;

            if (getChildIds().isEmpty() || getChildIds().contains(identifierProvider.getOid()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e)
    {
        if (this.childIds != null)
        {
            this.childIds.clear();
        }

        this.childList.clearSelection();

        if (e.getValueIsAdjusting())
        {
            return;
        }

        JList<?> parentList = (JList<?>) e.getSource();
        List<?> selectedObjects = parentList.getSelectedValuesList();

        Set<Long> parentIDs = new HashSet<>();

        for (Object selectedObject : selectedObjects)
        {
            if (selectedObject instanceof IdentifierProvider)
            {
                IdentifierProvider identifierProvider = (IdentifierProvider) selectedObject;
                parentIDs.add(identifierProvider.getOid());
            }
        }

        this.childIds = getCallback().getChildIDs(parentIDs);
        this.childFilterableEventList.update();

        try
        {
            this.childList.setSelectedIndex(0);
        }
        catch (Exception ex)
        {
            // NOOP
        }
    }
}
