package de.freese.base.swing.components.table;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Thomas Freese
 */
public final class TableExampleObservableList
{
    /**
     * @author Thomas Freese
     */
    private static class MyTableModel extends AbstractObservableListTableModel<int[]>
    {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -2601221304098179771L;

        /**
         * Erzeugt eine neue Instanz von {@link MyTableModel}.
         *
         * @param columnCount int
         * @param list {@link ObservableList}
         */
        MyTableModel(final int columnCount, final ObservableList<int[]> list)
        {
            super(columnCount, list);
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
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static void main(final String[] args) throws Exception
    {
        ObservableList<int[]> list = FXCollections.observableArrayList();

        JTable table = new JTable();
        table.setModel(new MyTableModel(5, list));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JFrame frame = new JFrame("TableExample1");
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
                chunks.forEach(list::add);
            }
        };

        swingWorker.execute();
    }

    /**
     * Erstellt ein neues {@link TableExampleObservableList} Object.
     */
    private TableExampleObservableList()
    {
        super();
    }
}
