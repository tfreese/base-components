package de.freese.base.swing.components.table.sort;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.components.table.column.ExtTableColumnModel;

/**
 * Kapselt die Sortierung für mehrere Spalten einer Tabelle.
 *
 * @author Thomas Freese
 */
public class TableColumnSorter {
    public static void add(final ExtTable table) {
        new TableColumnSorter(table);
    }

    // private final PropertyChangeSupport propertyChangeSupport;

    /**
     * @author Thomas Freese
     */
    private final class HeaderMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent event) {
            if (!table.isSortable()) {
                return;
            }

            final JTableHeader header = (JTableHeader) event.getSource();
            final TableColumnModel columnModel = header.getColumnModel();

            if (!(columnModel instanceof ExtTableColumnModel)) {
                return;
            }

            final int viewColumnIndex = columnModel.getColumnIndexAtX(event.getX());

            // Keine Tabellenspalte unter dem Cursor vorhanden
            if (viewColumnIndex == -1) {
                return;
            }

            final ExtTableColumn extTableColumn = (ExtTableColumn) columnModel.getColumn(viewColumnIndex);

            // Nicht sortierbare Spalte
            if (!extTableColumn.isSortable()) {
                return;
            }

            final int columnModelIndex = extTableColumn.getModelIndex();

            if (columnModelIndex != -1) {
                final Sort sort = extTableColumn.getSort();
                // System.out.println(sort.getPrevious() + "<-" + sort);
                // System.out.println(sort + "->" + sort.getNext());

                final Sort newSort = event.isShiftDown() ? sort.getPrevious() : sort.getNext();

                setSortStatus(extTableColumn, newSort);
            }
        }
    }

    private final List<ExtTableColumn> sortIndexList = new ArrayList<>();

    private final ExtTable table;

    /**
     * Der {@link TableColumnSorter} wird unter den Key {@code ROWSORTER} als ClientProperty der {@link JTable} registriert.
     */
    public TableColumnSorter(final ExtTable table) {
        super();

        if (table == null) {
            throw new NullPointerException();
        }

        // propertyChangeSupport = new PropertyChangeSupport(this);
        this.table = table;

        if (table.getClientProperty("ROWSORTER") != null) {
            throw new IllegalStateException("Tabelle hat bereits einen RowSorter !");
        }

        table.putClientProperty("ROWSORTER", this);

        final JTableHeader header = table.getTableHeader();
        header.addMouseListener(new HeaderMouseListener());
        header.setDefaultRenderer(new RowSorterHeaderRenderer(this, header.getDefaultRenderer()));
    }

    // public void addPropertyChangeListener(final PropertyChangeListener listener) {
    // getPropertyChangeSupport().addPropertyChangeListener(listener);
    // }
    //
    // private PropertyChangeSupport getPropertyChangeSupport() {
    // return propertyChangeSupport;
    // }

    /**
     * @return int, -1 = Keine Sortierung auf Spalte möglich, 0 = erste Spalte, 1 = zweite Spalte
     */
    public int getSortPriority(final ExtTableColumn tableColumnExt) {
        return sortIndexList.indexOf(tableColumnExt);
    }

    public void setSortStatus(final ExtTableColumn tableColumnExt, final Sort sort) {
        if (Sort.UNSORTED.equals(sort)) {
            sortIndexList.remove(tableColumnExt);
        }
        else if (Sort.ASCENDING.equals(sort)) {
            sortIndexList.add(tableColumnExt);
        }

        tableColumnExt.setSort(sort);

        // Runnable runnable = new Runnable() {
        // @Override
        // public void run() {
        // getPropertyChangeSupport().firePropertyChange("ROWSORTER_ORDER_CHANGED", null,
        // tableColumnExt);
        // }
        // };
        // SwingUtilities.invokeLater(runnable);
    }

    // public void removePropertyChangeListener(final PropertyChangeListener listener) {
    // getPropertyChangeSupport().removePropertyChangeListener(listener);
    // }
}
