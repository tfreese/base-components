package de.freese.base.swing.components.table.column;

import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Interface fuer ein erweitertes ColumnModel.
 *
 * @author Thomas Freese
 */
public interface IExtTableColumnModel extends TableColumnModel
{
    /**
     * Liefert die Anzahl der Spalten.
     *
     * @param includeHidden boolean
     * @return int
     */
    public int getColumnCount(boolean includeHidden);

    /**
     * Liefert die Spalte an der View-Position.
     *
     * @param columnIndex int
     * @return {@link ExtTableColumn}
     */
    public ExtTableColumn getColumnExt(int columnIndex);

    /**
     * Liefert die Spalte des Identifiers.
     *
     * @param identifier Object
     * @return ExtTableColumn
     */
    public ExtTableColumn getColumnExt(Object identifier);

    /**
     * Liefert die Spalten.
     *
     * @param includeHidden boolean
     * @return {@link List}
     */
    public List<TableColumn> getColumns(boolean includeHidden);

}
