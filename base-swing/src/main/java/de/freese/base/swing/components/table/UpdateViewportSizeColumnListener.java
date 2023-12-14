package de.freese.base.swing.components.table;

import java.util.Objects;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * @author Thomas Freese
 */
public class UpdateViewportSizeColumnListener implements TableColumnModelListener {
    private final JTable table;

    public UpdateViewportSizeColumnListener(final JTable table) {
        super();

        this.table = Objects.requireNonNull(table, "table required");
    }

    @Override
    public void columnAdded(final TableColumnModelEvent e) {
        updateViewportSize();
    }

    @Override
    public void columnMarginChanged(final ChangeEvent e) {
        updateViewportSize();
    }

    @Override
    public void columnMoved(final TableColumnModelEvent e) {
        updateViewportSize();
    }

    @Override
    public void columnRemoved(final TableColumnModelEvent e) {
        updateViewportSize();
    }

    @Override
    public void columnSelectionChanged(final ListSelectionEvent e) {
        // Empty
    }

    protected void updateViewportSize() {
        final JViewport view = (JViewport) this.table.getParent();
        final JScrollPane pane = (JScrollPane) view.getParent();

        pane.revalidate();
        view.setPreferredSize(this.table.getPreferredSize());
    }
}
