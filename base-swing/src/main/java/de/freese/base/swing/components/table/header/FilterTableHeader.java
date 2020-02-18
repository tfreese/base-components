package de.freese.base.swing.components.table.header;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import de.freese.base.swing.components.table.filter.TableFilterEditorHandler;
import de.freese.base.swing.filter.editor.FilterEditor;

/**
 * Headerpanel fuer die SpaltenFilter einer {@link JTable}.
 *
 * @author Thomas Freese
 */
public class FilterTableHeader extends JPanel
{
    /**
     * Listener reagiert auf Aenderungen des ColumnModels.
     *
     * @author Thomas Freese
     */
    private class FilterColumnModelListener implements TableColumnModelListener
    {
        /**
         * Erstellt ein neues {@link FilterColumnModelListener} Objekt.
         */
        public FilterColumnModelListener()
        {
            super();
        }

        /**
         * @see javax.swing.event.TableColumnModelListener#columnAdded(javax.swing.event.TableColumnModelEvent)
         */
        @Override
        public void columnAdded(final TableColumnModelEvent e)
        {
            reInitialize();
        }

        /**
         * @see javax.swing.event.TableColumnModelListener#columnMarginChanged(javax.swing.event.ChangeEvent)
         */
        @Override
        public void columnMarginChanged(final ChangeEvent e)
        {
            // NOOP
        }

        /**
         * @see javax.swing.event.TableColumnModelListener#columnMoved(javax.swing.event.TableColumnModelEvent)
         */
        @Override
        public void columnMoved(final TableColumnModelEvent e)
        {
            if (e.getFromIndex() != e.getToIndex())
            {
                reInitialize();
                FilterTableHeader.this.filterHeader.revalidate();
            }
        }

        /**
         * @see javax.swing.event.TableColumnModelListener#columnRemoved(javax.swing.event.TableColumnModelEvent)
         */
        @Override
        public void columnRemoved(final TableColumnModelEvent e)
        {
            reInitialize();
        }

        /**
         * @see javax.swing.event.TableColumnModelListener#columnSelectionChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void columnSelectionChanged(final ListSelectionEvent e)
        {
            // NOOP
        }
    }

    /**
     * Listener der die Breite einer Tabellenspalte mit der Breite der Filterkomponenten synchornisiert.
     *
     * @author Thomas Freese
     */
    private static class TableColumnListener implements PropertyChangeListener
    {
        /**
         * @param filterComponent {@link JComponent}
         * @param tc {@link TableColumn}c
         */
        @SuppressWarnings("unused")
        public static void add(final JComponent filterComponent, final TableColumn tc)
        {
            new TableColumnListener(filterComponent, tc);
        }

        /**
         *
         */
        private final JComponent filterComponent;

        /**
         *
         */
        private final TableColumn tc;

        /**
         * Erstellt ein neues TableColumnListener Objekt.
         *
         * @param filterComponent {@link JComponent}
         * @param tc {@link TableColumn}
         */
        private TableColumnListener(final JComponent filterComponent, final TableColumn tc)
        {
            super();

            this.tc = tc;
            this.filterComponent = filterComponent;
            tc.addPropertyChangeListener(this);
        }

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
            if (evt.getPropertyName().equals("width"))
            {
                // that property is updated with the table column width
                int w = this.tc.getWidth();
                Dimension d = new Dimension(w, this.filterComponent.getPreferredSize().height);

                this.filterComponent.setPreferredSize(d);

                // as we're on a flow layout, the subHeader will remain in sync with the table
                // header
                // as long as we maintain the same preferred sizes
                this.filterComponent.revalidate();
            }
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -1159400555125161760L;

    /**
     *
     */
    private final TableFilterEditorHandler filterHandler;

    /**
     * Panel mit den Spaltenfiltern.
     */
    private final JPanel filterHeader;

    /**
     * Erstellt ein neues {@link FilterTableHeader} Object.
     *
     * @param filterHandler {@link TableFilterEditorHandler}
     */
    public FilterTableHeader(final TableFilterEditorHandler filterHandler)
    {
        super();

        this.filterHandler = filterHandler;

        setLayout(new BorderLayout());

        this.filterHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTable table = this.filterHandler.getTable();

        if (table.getTableHeader() != null)
        {
            add(table.getTableHeader(), BorderLayout.CENTER);
        }

        reInitialize();
        add(this.filterHeader, BorderLayout.SOUTH);
        table.getColumnModel().addColumnModelListener(new FilterColumnModelListener());
    }

    /**
     * Initialisiert die Oberflaeche.
     */
    private void reInitialize()
    {
        this.filterHeader.removeAll();

        JTable table = this.filterHandler.getTable();

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
        {
            int modelColumnIndex = table.convertColumnIndexToModel(i);
            final FilterEditor filterEditor = this.filterHandler.getFilterEditor(modelColumnIndex);

            JComponent filterEditorComponent = null;

            if (filterEditor == null)
            {
                // filterEditorComponent = (JComponent) Box.createGlue();
                filterEditorComponent = new JComponent()
                {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                };
            }
            else
            {
                filterEditorComponent = filterEditor.getComponent();
            }

            int w = table.getColumnModel().getColumn(i).getWidth();
            Dimension d = new Dimension(w, filterEditorComponent.getPreferredSize().height);
            filterEditorComponent.setPreferredSize(d);

            // listen to width change to adapt the subHeader
            TableColumn tc = table.getColumnModel().getColumn(i);
            TableColumnListener.add(filterEditorComponent, tc);
            this.filterHeader.add(filterEditorComponent);
        }
    }
}
