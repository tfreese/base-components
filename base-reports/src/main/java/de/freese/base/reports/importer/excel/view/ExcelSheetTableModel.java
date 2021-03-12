package de.freese.base.reports.importer.excel.view;

import javax.swing.table.AbstractTableModel;
import de.freese.base.reports.importer.excel.ExcelSheet;

/**
 * Tabelmodell eines ExcelSheets.
 *
 * @author Thomas Freese
 */
public class ExcelSheetTableModel extends AbstractTableModel
{
    /**
     *
     */
    private static final long serialVersionUID = -9093380478461819827L;

    /**
     *
     */
    private ExcelSheet excelSheet;

    /**
     * Creates a new {@link ExcelSheetTableModel} object.
     *
     * @param excelSheet {@link ExcelSheet}
     */
    public ExcelSheetTableModel(final ExcelSheet excelSheet)
    {
        super();

        this.excelSheet = excelSheet;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount()
    {
        return this.excelSheet.getColumnCount();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int column)
    {
        // Die erste Spalte hat keinen Namen in Excel.
        if (column == 0)
        {
            // Leerzeichen im String verhindert, das die Header eine zu flache Höhe haben.
            return " ";
        }

        // -1: Damit zweite Spalte mit A anfaengt.
        return super.getColumnName(column - 1);
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount()
    {
        return this.excelSheet.getRowCount();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        if (columnIndex == 0)
        {
            // return new JLabel(new Integer(row+1).toString());
            return rowIndex + 1;
        }

        if ((columnIndex < getColumnCount()) && (rowIndex < getRowCount()))
        {
            return this.excelSheet.getValueAt(rowIndex, columnIndex - 1);
        }

        return "";
    }
}
