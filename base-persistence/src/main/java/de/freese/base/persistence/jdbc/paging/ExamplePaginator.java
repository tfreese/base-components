// Created: 21 Okt. 2025
package de.freese.base.persistence.jdbc.paging;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ExamplePaginator implements Paginator<LocalDateTime> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePaginator.class);
    private static final int MAX_ROWS = 100;

    private static final class PaginatorTableModel<T> extends AbstractTableModel {
        private static final int LIMIT = 10;
        @Serial
        private static final long serialVersionUID = 1146960307197901805L;
        private final transient List<T> list = new ArrayList<>();
        private final transient Paginator<T> paginator;
        private final Semaphore semaphore = new Semaphore(0, true);

        private int offset;
        private boolean populated;

        PaginatorTableModel(final Paginator<T> paginator) {
            super();

            this.paginator = Objects.requireNonNull(paginator, "paginator required");
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final Object object = list.get(rowIndex);

            if (rowIndex + 1 == getRowCount() && !populated && semaphore.availablePermits() > 0) {
                semaphore.acquireUninterruptibly();

                Thread.startVirtualThread(this::nextPage);
                // Thread.ofVirtual().name("LoadPage").start(this::nextPage);
            }

            if (columnIndex == 0) {
                return rowIndex + 1;
            }

            return object;
        }

        public void nextPage() {
            try {
                final List<T> newRows = paginator.getPage(offset, LIMIT);

                LOGGER.info("Next Page: {} -> {}", offset, offset + newRows.size());

                if (newRows.isEmpty()) {
                    populated = true;
                    return;
                }

                offset += newRows.size();

                final int firstRowIndex = getRowCount();

                list.addAll(newRows);

                SwingUtilities.invokeLater(() -> fireTableRowsInserted(firstRowIndex, getRowCount()));
            }
            finally {
                semaphore.release();
            }
        }
    }

    static void main() {
        final JFrame jFrame = new JFrame("Paginator");
        jFrame.setSize(500, 200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JScrollPane jScrollPane = new JScrollPane();
        jFrame.setContentPane(jScrollPane);

        final JTable jTable = new JTable();
        jScrollPane.setViewportView(jTable);

        final Paginator<LocalDateTime> paginator = new ExamplePaginator();

        final PaginatorTableModel<LocalDateTime> tableModel = new PaginatorTableModel<>(paginator);

        jTable.setModel(tableModel);
        jFrame.setVisible(true);

        SwingUtilities.invokeLater(tableModel::nextPage);
    }

    private int generatedRows;

    @Override
    public List<LocalDateTime> getPage(final int offset, final int limit) {
        if (generatedRows == MAX_ROWS) {
            return List.of();
        }

        int rowsToGenerate = limit;

        if (generatedRows + rowsToGenerate > MAX_ROWS) {
            rowsToGenerate = MAX_ROWS - generatedRows;
        }

        LOGGER.info("Generating {} elements", rowsToGenerate);

        generatedRows += rowsToGenerate;

        return IntStream.range(0, rowsToGenerate).mapToObj(i -> LocalDateTime.now()).toList();
    }
}
