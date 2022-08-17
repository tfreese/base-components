package de.freese.base.swing.components.table;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import de.freese.base.swing.components.table.column.ExtTableColumn;
import de.freese.base.swing.components.table.column.ExtTableColumnModelListenerAdapter;
import de.freese.base.swing.eventlist.DefaultEventList;
import de.freese.base.swing.eventlist.EventList;

/**
 * @author Thomas Freese
 */
public final class TableExampleEventList
{
    /**
     * @author Thomas Freese
     */
    private static class MyTableModel extends AbstractEventListTableModel<int[]>
    {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 6289962608942473870L;

        /**
         * Erstellt ein neues {@link MyTableModel} Object.
         *
         * @param list {@link EventList}
         */
        MyTableModel(final EventList<int[]> list)
        {
            super(5, list);
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex)
        {
            int[] row = getObjectAt(rowIndex);

            return row[columnIndex];
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        DefaultEventList<int[]> eventList = new DefaultEventList<>();

        MyTableModel tableModel = new MyTableModel(eventList);
        ExtTable table = new ExtTable();
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ExtTableColumn extTableColumn = table.getColumnModelExt().getColumnExt(0);
        extTableColumn.setVisibleChange(false);
        extTableColumn.setSortable(false);

        table.getColumnModel().addColumnModelListener(new ExtTableColumnModelListenerAdapter()
        {
            /**
             * @see de.freese.base.swing.components.table.column.ExtTableColumnModelListenerAdapter#columnPropertyChange(java.beans.PropertyChangeEvent)
             */
            @Override
            public void columnPropertyChange(final PropertyChangeEvent event)
            {
                if ("sort".equals(event.getPropertyName()))
                {
                    System.out.println("TableExample0.columnPropertyChange.sort: " + event.getSource());
                }
                else if ("visible".equals(event.getPropertyName()))
                {
                    System.out.println("TableExample0.columnPropertyChange.visible: " + event.getSource());
                }
            }
        });

        JFrame frame = new JFrame("TableExample0");
        frame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent we)
            {
                System.exit(0);
            }
        });

        frame.getContentPane().add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);

        SwingWorker<Void, int[]> swingWorker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected Void doInBackground() throws Exception
            {
                for (int i = 1; i < 6; i++)
                {
                    publish(new int[]
                            {
                                    i, 2, 3, 4, 5
                            });

                    TimeUnit.MILLISECONDS.sleep(2000);
                }

                return null;
            }

            /**
             * @see javax.swing.SwingWorker#process(java.util.List)
             */
            @Override
            protected void process(final List<int[]> chunks)
            {
                chunks.forEach(eventList::add);
            }
        };

        swingWorker.execute();
    }

    /**
     * Erstellt ein neues {@link TableExampleEventList} Object.
     */
    private TableExampleEventList()
    {
        super();
    }
}
