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

        this.adapter = action::setEnabled;

        this.adapter.setEnabled(false);
    }

    public EnableDisableSelectionListener(final Component component) {
        super();

        this.adapter = component::setEnabled;

        this.adapter.setEnabled(false);
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }

        Object source = event.getSource();

        ListSelectionModel selectionModel = null;

        if (source instanceof JList<?> l) {
            selectionModel = l.getSelectionModel();
        }
        else if (source instanceof ListSelectionModel m) {
            selectionModel = m;
        }

        this.adapter.setEnabled(componentEnabled(event, selectionModel));
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
    public void valueChanged(final TreeSelectionEvent event) {
        Object source = event.getSource();

        TreeSelectionModel selectionModel = null;

        if (source instanceof JTree t) {
            selectionModel = t.getSelectionModel();
        }
        else if (source instanceof TreeSelectionModel m) {
            selectionModel = m;
        }

        this.adapter.setEnabled(componentEnabled(event, selectionModel));
    }

    protected boolean componentEnabled(final ListSelectionEvent event, final ListSelectionModel selectionModel) {
        return !selectionModel.isSelectionEmpty();
    }

    protected boolean componentEnabled(final TreeSelectionEvent event, final TreeSelectionModel selectionModel) {
        return !selectionModel.isSelectionEmpty();
    }
}
