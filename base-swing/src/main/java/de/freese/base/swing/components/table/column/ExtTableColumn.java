package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.freese.base.swing.components.table.sort.Sort;

/**
 * Erweiterte {@link TableColumn} die das sortieren und ausblenden unterstützt.
 *
 * @author Thomas Freese
 */
public class ExtTableColumn extends TableColumn {
    @Serial
    private static final long serialVersionUID = 4220187542201364522L;

    private transient Map<Object, Object> clientProperties;

    private Sort sort = Sort.UNSORTED;
    private boolean sortable = true;
    private boolean visible = true;
    private boolean visibleChange = true;

    public ExtTableColumn() {
        super();
    }

    public ExtTableColumn(final int modelIndex) {
        super(modelIndex);
    }

    public ExtTableColumn(final int modelIndex, final int width) {
        super(modelIndex, width);
    }

    public ExtTableColumn(final int modelIndex, final int width, final TableCellRenderer cellRenderer, final TableCellEditor cellEditor) {
        super(modelIndex, width, cellRenderer, cellEditor);
    }

    /**
     * Returns the value of the property with the specified key. Only properties added with <code>putClientProperty</code> will return a non-<code>null</code>
     * value.
     *
     * @param key Object which is used as a key to retrieve value
     *
     * @return Object containing value of client property or <code>null</code>
     */
    public Object getClientProperty(final Object key) {
        return (key == null || clientProperties == null) ? null : clientProperties.get(key);
    }

    @Override
    public boolean getResizable() {
        return super.getResizable() && getMinWidth() < getMaxWidth();
    }

    public Sort getSort() {
        return sort;
    }

    public String getTitle() {
        final Object header = getHeaderValue();

        return header != null ? header.toString() : null;
    }

    public boolean isSortable() {
        return sortable;
    }

    /**
     * Returns the visible property. The default is <code>true</code>.
     *
     * @return boolean indicating whether this view column is visible in the table
     */
    public boolean isVisible() {
        return visible;
    }

    public boolean isVisibleChange() {
        return visibleChange;
    }

    /**
     * Sets the client property "key" to <code>value</code>. If <code>value</code> is <code>null</code> this method will remove the property. Changes to client
     * properties are reported with <code>PropertyChange</code> events. The name of the property (for the sake of PropertyChange events) is
     * <code>key.toString()</code>.<br>
     * <br>
     * The <code>get/putClientProperty</code> methods provide access to a per-instance hashtable, which is intended for small scale extensions of TableColumn.
     *
     * @param key Object which is used as key to retrieve value
     * @param value Object containing value of client property
     *
     * @throws IllegalArgumentException if key is <code>null</code>
     */
    public void putClientProperty(final Object key, final Object value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        if (value == null && getClientProperty(key) == null) {
            return;
        }

        final Object old = getClientProperty(key);

        if (value == null) {
            getClientProperties().remove(key);
        }
        else {
            getClientProperties().put(key, value);
        }

        firePropChange(key.toString(), old, value);
    }

    public void setSort(final Sort sort) {
        final Sort oldSort = this.sort;
        this.sort = sort;

        firePropChange("sort", oldSort, sort);
    }

    public void setSortable(final boolean sortable) {
        this.sortable = sortable;
    }

    public void setTitle(final String title) {
        setHeaderValue(title);
    }

    /**
     * Sets the visible property. This property controls whether this view column is currently visible in the table.
     *
     * @param visible boolean indicating whether this view column is visible in the table
     */
    public void setVisible(final boolean visible) {
        final boolean oldVisible = this.visible;
        this.visible = visible;

        firePropChange("visible", oldVisible, visible);
    }

    public void setVisibleChange(final boolean visibleChange) {
        final boolean oldVisibleChange = this.visibleChange;
        this.visibleChange = visibleChange;

        firePropChange("visible_change", oldVisibleChange, visibleChange);
    }

    /**
     * Notifies registered <code>PropertyChangeListener</code>s about property changes. This method must be invoked internally whe any of the enhanced
     * properties changed.<br>
     * <br>
     * Implementation note: needed to replicate super functionality because super's field <code>propertyChangeSupport</code> and method <code>fireXX</code> are
     * both private.
     *
     * @param propertyName name of changed property
     * @param oldValue old value of changed property
     * @param newValue new value of changed property
     */
    protected void firePropChange(final String propertyName, final Object oldValue, final Object newValue) {
        if (oldValue != null && !oldValue.equals(newValue) || oldValue == null && newValue != null) {
            final PropertyChangeListener[] pcl = getPropertyChangeListeners();

            if (pcl != null && pcl.length != 0) {
                final PropertyChangeEvent pce = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

                for (PropertyChangeListener element : pcl) {
                    element.propertyChange(pce);
                }
            }
        }
    }

    private Map<Object, Object> getClientProperties() {
        if (clientProperties == null) {
            clientProperties = new HashMap<>();
        }

        return clientProperties;
    }
}
