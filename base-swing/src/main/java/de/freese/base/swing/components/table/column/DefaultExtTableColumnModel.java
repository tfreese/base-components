package de.freese.base.swing.components.table.column;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Defaultimplementierung des erweiterten {@link TableColumnModel}.
 *
 * @author Thomas Freese
 */
public class DefaultExtTableColumnModel extends DefaultTableColumnModel implements ExtTableColumnModel {
    /**
     * flag to distinguish a shown/hidden column from really added/removed columns during notification. This is brittle!
     */
    private static final String IGNORE_EVENT = "TableColumnModelExt.ignoreEvent";

    @Serial
    private static final long serialVersionUID = 7120329832987702244L;

    private final class VisibilityListener implements PropertyChangeListener, Serializable {
        @Serial
        private static final long serialVersionUID = -5146125743167750699L;

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if ("visible".equals(evt.getPropertyName())) {
                final ExtTableColumn columnExt = (ExtTableColumn) evt.getSource();

                if (columnExt.isVisible()) {
                    moveToVisible(columnExt);
                    fireColumnPropertyChange(evt);
                }
                else {
                    moveToInvisible(columnExt);
                }
            }
            else if (!((ExtTableColumn) evt.getSource()).isVisible()) {
                fireColumnPropertyChange(evt);
            }
        }
    }

    /**
     * contains a list of all column, in the order they would appear if all were visible.
     */
    private final transient List<TableColumn> currentColumns = new ArrayList<>();
    /**
     * contains a list of all columns, in the order in which were added to the model.
     */
    private final transient List<TableColumn> initialColumns = new ArrayList<>();
    /**
     * Listener attached to TableColumnExt instances to listen for changes to their visibility status, and to hide/show the column as appropriate
     */
    private final VisibilityListener visibilityListener = new VisibilityListener();

    @Override
    public void addColumn(final TableColumn aColumn) {
        // Hacking to guarantee correct events.
        // two-step: add as visible, setVisible
        boolean oldVisible = true;

        // Add the visibility listener if appropriate.
        if (aColumn instanceof ExtTableColumn xColumn) {
            oldVisible = xColumn.isVisible();
            xColumn.setVisible(true);
            xColumn.addPropertyChangeListener(visibilityListener);
        }

        // Append the column to the end of both initial- and currentColumns.
        currentColumns.add(aColumn);
        initialColumns.add(aColumn);

        // Let super Method handle the event notification.
        super.addColumn(aColumn);

        if (aColumn instanceof ExtTableColumn c) {
            // reset original visibility
            c.setVisible(oldVisible);
        }
    }

    @Override
    public void addColumnModelListener(final TableColumnModelListener listener) {
        super.addColumnModelListener(listener);

        if (listener instanceof ExtTableColumnModelListener l) {
            listenerList.add(ExtTableColumnModelListener.class, l);
        }
    }

    @Override
    public int getColumnCount(final boolean includeHidden) {
        if (includeHidden) {
            return initialColumns.size();
        }

        return getColumnCount();
    }

    @Override
    public ExtTableColumn getColumnExt(final int columnIndex) {
        final TableColumn column = getColumn(columnIndex);

        if (column instanceof ExtTableColumn c) {
            return c;
        }

        return null;
    }

    @Override
    public ExtTableColumn getColumnExt(final Object identifier) {
        for (TableColumn column : initialColumns) {
            if (column instanceof ExtTableColumn c && identifier.equals(column.getIdentifier())) {
                return c;
            }
        }

        return null;
    }

    @Override
    public List<TableColumn> getColumns(final boolean includeHidden) {
        if (includeHidden) {
            return new ArrayList<>(initialColumns);
        }

        return Collections.list(getColumns());
    }

    public ExtTableColumnModelListener[] getTableColumnModelExtListeners() {
        return listenerList.getListeners(ExtTableColumnModelListener.class);
    }

    /**
     * hot fix for #157: listeners that are aware of the possible existence of invisible columns should check if the received columnAdded originated from moving
     * a column from invisible to visible.
     *
     * @param newIndex the toIndex of the columnEvent
     *
     * @return true if the column was moved to visible
     */
    public boolean isAddedFromInvisibleEvent(final int newIndex) {
        if (!(getColumn(newIndex) instanceof ExtTableColumn)) {
            return false;
        }

        return Boolean.TRUE.equals(((ExtTableColumn) getColumn(newIndex)).getClientProperty(IGNORE_EVENT));
    }

    /**
     * hot fix for #157: listeners that are aware of the possible existence of invisible columns should check if the received columnRemoved originated from
     * moving a column from visible to invisible.
     *
     * @param oldIndex the fromIndex of the columnEvent
     *
     * @return true if the column was moved to invisible
     */
    public boolean isRemovedToInvisibleEvent(final int oldIndex) {
        if (oldIndex >= currentColumns.size() || !(currentColumns.get(oldIndex) instanceof ExtTableColumn)) {
            return false;
        }

        return Boolean.TRUE.equals(((ExtTableColumn) currentColumns.get(oldIndex)).getClientProperty(IGNORE_EVENT));
    }

    @Override
    public void moveColumn(final int columnIndex, final int newIndex) {
        if (columnIndex != newIndex) {
            updateCurrentColumns(columnIndex, newIndex);
        }

        super.moveColumn(columnIndex, newIndex);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        super.propertyChange(evt);

        fireColumnPropertyChange(evt);
    }

    @Override
    public void removeColumn(final TableColumn column) {
        if (column instanceof ExtTableColumn c) {
            c.removePropertyChangeListener(visibilityListener);
        }

        currentColumns.remove(column);
        initialColumns.remove(column);

        super.removeColumn(column);
    }

    @Override
    public void removeColumnModelListener(final TableColumnModelListener listener) {
        super.removeColumnModelListener(listener);

        if (listener instanceof ExtTableColumnModelListener l) {
            listenerList.remove(ExtTableColumnModelListener.class, l);
        }
    }

    /**
     * Notifies <code>TableColumnModelExtListener</code>s about property changes of contained columns. The event instance is the original as fired by the
     * <code>TableColumn</code>.
     */
    protected void fireColumnPropertyChange(final PropertyChangeEvent evt) {
        if (IGNORE_EVENT.equals(evt.getPropertyName())) {
            return;
        }

        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ExtTableColumnModelListener.class) {
                ((ExtTableColumnModelListener) listeners[i + 1]).columnPropertyChange(evt);
            }
        }
    }

    /**
     * Update internal state after the visibility of the column was changed to invisible. The given column is assumed to be contained in this model.
     *
     * @param col the column which was hidden.
     */
    protected void moveToInvisible(final ExtTableColumn col) {
        col.putClientProperty(IGNORE_EVENT, Boolean.TRUE);
        super.removeColumn(col);
        col.putClientProperty(IGNORE_EVENT, null);
    }

    /**
     * Update internal state after the visibility of the column was changed to visible. The given column is assumed to be contained in this model.
     *
     * @param col the column which was made visible.
     */
    protected void moveToVisible(final ExtTableColumn col) {
        col.putClientProperty(IGNORE_EVENT, Boolean.TRUE);

        // two-step process: first add at the end of columns
        // then move to "best" position relative to where it
        // was before hiding.
        super.addColumn(col);

        // this is analogous to the proposed fix in #253-swingx
        // but uses the currentColumns as reference.
        final int addIndex = currentColumns.indexOf(col);

        for (int i = 0; i < (getColumnCount() - 1); i++) {
            final TableColumn tableCol = getColumn(i);
            final int actualPosition = currentColumns.indexOf(tableCol);

            if (actualPosition > addIndex) {
                super.moveColumn(getColumnCount() - 1, i);
                break;
            }
        }

        col.putClientProperty(IGNORE_EVENT, null);
    }

    /**
     * Adjusts the current column sequence when a visible column is moved.
     *
     * @param oldIndex the old visible position.
     * @param newIndex the new visible position.
     */
    private void updateCurrentColumns(final int oldIndex, final int newIndex) {
        final TableColumn movedColumn = tableColumns.elementAt(oldIndex);
        final int oldPosition = currentColumns.indexOf(movedColumn);

        final TableColumn targetColumn = tableColumns.elementAt(newIndex);
        final int newPosition = currentColumns.indexOf(targetColumn);

        currentColumns.remove(oldPosition);
        currentColumns.add(newPosition, movedColumn);
    }
}
