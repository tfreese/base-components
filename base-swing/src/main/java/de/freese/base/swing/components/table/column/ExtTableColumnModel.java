package de.freese.base.swing.components.table.column;

import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Interface für ein erweitertes ColumnModel.
 *
 * @author Thomas Freese
 */
public interface ExtTableColumnModel extends TableColumnModel {
    int getColumnCount(boolean includeHidden);

    ExtTableColumn getColumnExt(int columnIndex);

    ExtTableColumn getColumnExt(Object identifier);

    List<TableColumn> getColumns(boolean includeHidden);
}
