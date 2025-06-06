package de.freese.base.swing.listener;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Thomas Freese
 */
public class EnableDisableSelectionListener implements ListSelectionListener, TreeSelectionListener {
    /**
     * @author Thomas Freese
     */
    @FunctionalInterface
    private interface EnablerAdapter {
        void setEnabled(boolean value);
    }

    private final EnablerAdapter adapter;

    public EnableDisableSelectionListener(final Action action) {
        super();

        adapter = action::setEnabled;

        adapter.setEnabled(false);
    }

    public EnableDisableSelectionListener(final Component component) {
        super();

        adapter = component::setEnabled;

        adapter.setEnabled(false);
    }

    @Override
    public void valueChanged(final ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }

        final Object source = event.getSource();

        ListSelectionModel selectionModel = null;

        if (source instanceof JList<?> l) {
            selectionModel = l.getSelectionModel();
        }
        else if (source instanceof ListSelectionModel m) {
            selectionModel = m;
        }

        adapter.setEnabled(componentEnabled(event, selectionModel));
    }

    @Override
    public void valueChanged(final TreeSelectionEvent event) {
        final Object source = event.getSource();

        TreeSelectionModel selectionModel = null;

        if (source instanceof JTree t) {
            selectionModel = t.getSelectionModel();
        }
        else if (source instanceof TreeSelectionModel m) {
            selectionModel = m;
        }

        adapter.setEnabled(componentEnabled(event, selectionModel));
    }

    protected boolean componentEnabled(final ListSelectionEvent event, final ListSelectionModel selectionModel) {
        return !selectionModel.isSelectionEmpty();
    }

    protected boolean componentEnabled(final TreeSelectionEvent event, final TreeSelectionModel selectionModel) {
        return !selectionModel.isSelectionEmpty();
    }
}
