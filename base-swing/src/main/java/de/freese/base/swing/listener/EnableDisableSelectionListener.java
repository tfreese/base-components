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
 * Diese Klasse dient als {@link ListSelectionListener} oder {@link TreeSelectionListener}. Sie deaktiviert oder aktiviert eine Componente/Action, je nachdem ob
 * eine Auswahl in einem {@link ListSelectionModel} oder {@link TreeSelectionListener} getroffen wurde.
 *
 * @author Thomas Freese
 */
public class EnableDisableSelectionListener implements ListSelectionListener, TreeSelectionListener
{
    /**
     * Interface als Adapter um Componenten oder Actions zu kapseln.
     *
     * @author Thomas Freese
     */
    @FunctionalInterface
    private interface EnablerAdapter
    {
        /**
         * @param value boolean
         */
        public void setEnabled(boolean value);
    }

    /**
     *
     */
    private final EnablerAdapter adapter;

    /**
     * Creates a new {@link EnableDisableSelectionListener} object.
     *
     * @param action {@link Action}
     */
    public EnableDisableSelectionListener(final Action action)
    {
        super();

        this.adapter = value -> action.setEnabled(value);

        this.adapter.setEnabled(false);
    }

    /**
     * Creates a new {@link EnableDisableSelectionListener} object.
     *
     * @param component {@link Component}
     */
    public EnableDisableSelectionListener(final Component component)
    {
        super();

        this.adapter = value -> component.setEnabled(value);

        this.adapter.setEnabled(false);
    }

    /**
     * Bestimmt, ob die Komponente aktiviert werden soll.
     *
     * @param e {@link ListSelectionEvent}
     * @param selectionModel {@link ListSelectionModel}
     * @return <code>true</code> wenn ja, sonst <code>false</code>
     */
    protected boolean componentEnabled(final ListSelectionEvent e, final ListSelectionModel selectionModel)
    {
        // Moeglicher Nullpointer wird hier bewusst NICHT abgefangen !
        return !selectionModel.isSelectionEmpty();
    }

    /**
     * Bestimmt, ob die Komponente aktiviert werden soll.
     *
     * @param e {@link TreeSelectionEvent}
     * @param selectionModel {@link TreeSelectionModel}
     * @return <code>true</code> wenn ja, sonst <code>false</code>
     */
    protected boolean componentEnabled(final TreeSelectionEvent e, final TreeSelectionModel selectionModel)
    {
        // Moeglicher Nullpointer wird hier bewusst NICHT abgefangen !
        return !selectionModel.isSelectionEmpty();
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting())
        {
            return;
        }

        Object source = e.getSource();

        ListSelectionModel selectionModel = null;

        if (source instanceof JList)
        {
            selectionModel = ((JList<?>) source).getSelectionModel();
        }
        else if (source instanceof ListSelectionModel)
        {
            selectionModel = (ListSelectionModel) source;
        }

        this.adapter.setEnabled(componentEnabled(e, selectionModel));
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
    public void valueChanged(final TreeSelectionEvent e)
    {
        Object source = e.getSource();

        TreeSelectionModel selectionModel = null;

        if (source instanceof JTree)
        {
            selectionModel = ((JTree) source).getSelectionModel();
        }
        else if (source instanceof TreeSelectionModel)
        {
            selectionModel = (TreeSelectionModel) source;
        }

        this.adapter.setEnabled(componentEnabled(e, selectionModel));
    }
}
