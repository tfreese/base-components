package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import de.freese.base.swing.components.table.sort.Sort;

/**
 * Erweiterte {@link TableColumn} die das sortieren und ausblenden unterstuetzt.
 *
 * @author Thomas Freese
 */
public class ExtTableColumn extends TableColumn
{
    /**
     *
     */
    private static final long serialVersionUID = 4220187542201364522L;

    /**
     *
     */
    private Map<Object, Object> clientProperties;

    /**
     *
     */
    private Sort sort = Sort.UNSORTED;

    /**
     *
     */
    private boolean sortable = true;

    /**
     *
     */
    private boolean visible = true;

    /**
     *
     */
    private boolean visibleChange = true;

    /**
     * Erstellt ein neues {@link ExtTableColumn} Object.
     */
    public ExtTableColumn()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link ExtTableColumn} Object.
     *
     * @param modelIndex int
     */
    public ExtTableColumn(final int modelIndex)
    {
        super(modelIndex);
    }

    /**
     * Erstellt ein neues {@link ExtTableColumn} Object.
     *
     * @param modelIndex int
     * @param width int
     */
    public ExtTableColumn(final int modelIndex, final int width)
    {
        super(modelIndex, width);
    }

    /**
     * Erstellt ein neues {@link ExtTableColumn} Object.
     *
     * @param modelIndex int
     * @param width int
     * @param cellRenderer {@link TableCellRenderer}
     * @param cellEditor {@link TableCellEditor}
     */
    public ExtTableColumn(final int modelIndex, final int width, final TableCellRenderer cellRenderer, final TableCellEditor cellEditor)
    {
        super(modelIndex, width, cellRenderer, cellEditor);
    }

    /**
     * Notifies registered <code>PropertyChangeListener</code>s about property changes. This method must be invoked internally whe any of the enhanced
     * properties changed.
     * <p>
     * Implementation note: needed to replicate super functionality because super's field <code>propertyChangeSupport</code> and method <code>fireXX</code> are
     * both private.
     *
     * @param propertyName name of changed property
     * @param oldValue old value of changed property
     * @param newValue new value of changed property
     */
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
    {
        if (((oldValue != null) && !oldValue.equals(newValue)) || ((oldValue == null) && (newValue != null)))
        {
            PropertyChangeListener pcl[] = getPropertyChangeListeners();

            if ((pcl != null) && (pcl.length != 0))
            {
                PropertyChangeEvent pce = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

                for (PropertyChangeListener element : pcl)
                {
                    element.propertyChange(pce);
                }
            }
        }
    }

    /**
     * @return {@link Map}
     */
    private Map<Object, Object> getClientProperties()
    {
        if (this.clientProperties == null)
        {
            this.clientProperties = new HashMap<>();
        }

        return this.clientProperties;
    }

    /**
     * Returns the value of the property with the specified key. Only properties added with <code>putClientProperty</code> will return a non-<code>null</code>
     * value.
     *
     * @param key Object which is used as key to retrieve value
     * @return Object containing value of client property or <code>null</code>
     * @see #putClientProperty
     */
    public Object getClientProperty(final Object key)
    {
        return ((key == null) || (this.clientProperties == null)) ? null : this.clientProperties.get(key);
    }

    /**
     * @see javax.swing.table.TableColumn#getResizable()
     */
    @Override
    public boolean getResizable()
    {
        return super.getResizable() && (getMinWidth() < getMaxWidth());
    }

    /**
     * Aktualle Sortierung.
     *
     * @return {@link Sort}
     */
    public Sort getSort()
    {
        return this.sort;
    }

    /**
     * Liefert den Headertext.
     *
     * @return String
     */
    public String getTitle()
    {
        Object header = getHeaderValue();

        return header != null ? header.toString() : null;
    }

    /**
     * @return boolean
     */
    public boolean isSortable()
    {
        return this.sortable;
    }

    /**
     * Returns the visible property. The default is <code>true</code>.
     *
     * @return boolean indicating whether or not this view column is visible in the table
     * @see #setVisible
     */
    public boolean isVisible()
    {
        return this.visible;
    }

    /**
     * Aenderung der Sichtbarkeit erlaubt ?
     *
     * @return boolean
     */
    public boolean isVisibleChange()
    {
        return this.visibleChange;
    }

    /**
     * Sets the client property "key" to <code>value</code>. If <code>value</code> is <code>null</code> this method will remove the property. Changes to client
     * properties are reported with <code>PropertyChange</code> events. The name of the property (for the sake of PropertyChange events) is
     * <code>key.toString()</code>.
     * <p>
     * The <code>get/putClientProperty</code> methods provide access to a per-instance hashtable, which is intended for small scale extensions of TableColumn.
     * <p>
     *
     * @param key Object which is used as key to retrieve value
     * @param value Object containing value of client property
     * @throws IllegalArgumentException if key is <code>null</code>
     * @see #getClientProperty
     * @see javax.swing.JComponent#putClientProperty
     */
    public void putClientProperty(final Object key, final Object value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("null key");
        }

        if ((value == null) && (getClientProperty(key) == null))
        {
            return;
        }

        Object old = getClientProperty(key);

        if (value == null)
        {
            getClientProperties().remove(key);
        }
        else
        {
            getClientProperties().put(key, value);
        }

        firePropertyChange(key.toString(), old, value);
    }

    /**
     * Aktualle Sortierung.
     *
     * @param sort {@link Sort}
     */
    public void setSort(final Sort sort)
    {
        Sort oldSort = this.sort;
        this.sort = sort;

        firePropertyChange("sort", oldSort, sort);
    }

    /**
     * @param sortable boolean
     */
    public void setSortable(final boolean sortable)
    {
        this.sortable = sortable;
    }

    /**
     * Setzt den Headertext.
     *
     * @param title String
     */
    public void setTitle(final String title)
    {
        setHeaderValue(title);
    }

    /**
     * Sets the visible property. This property controls whether or not this view column is currently visible in the table.
     *
     * @param visible boolean indicating whether or not this view column is visible in the table
     * @see #setVisible
     */
    public void setVisible(final boolean visible)
    {
        boolean oldVisible = this.visible;
        this.visible = visible;

        firePropertyChange("visible", Boolean.valueOf(oldVisible), Boolean.valueOf(visible));
    }

    /**
     * Ae‚nderung der Sichtbarkeit erlaubt ?
     *
     * @param visibleChange boolean
     */
    public void setVisibleChange(final boolean visibleChange)
    {
        boolean oldVisibleChange = this.visibleChange;
        this.visibleChange = visibleChange;

        firePropertyChange("visible_change", Boolean.valueOf(oldVisibleChange), Boolean.valueOf(visibleChange));
    }
}
