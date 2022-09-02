package de.freese.base.reports.importer.excel;

import java.awt.Rectangle;
import java.util.StringTokenizer;

import javax.swing.JTable;

/**
 * Toolkit für Excel-Funktionalitäten.
 *
 * @author Thomas Freese
 */
public final class ExcelToolkit
{
    /**
     * Ergibt bei 6,5 = F5.
     *
     * @param column int
     * @param row int
     *
     * @return String
     */
    public static synchronized String getCellName(final int column, final int row)
    {
        return getColumnName(column) + row;
    }

    /**
     * Liefert die Spaltennamen, zB 2 -> B.
     *
     * @param column int
     *
     * @return Spaltenname als Excel Spaltenname
     */
    public static String getColumnName(final int column)
    {
        // Die erste Spalte hat keinen Namen in Excel.
        if (column == 0)
        {
            // Leerzeichen im String verhindert, das die Header eine zu flache Höhe haben.
            return " ";
        }

        // Weil erste Spalte die Row-Numbers enthält und somit KEINEN Namen hat.
        int c = column - 1;

        StringBuilder sb = new StringBuilder();

        for (; c >= 0; c = (c / 26) - 1)
        {
            sb.append((char) (c % 26) + 'A');
        }

        return sb.toString();

        // column--;
        //
        // int a = column / 26;
        // int b = column % 26;
        //
        // // 'A' = 65
        // if (a == 0)
        // {
        // return Character.toString((char) (column + 65));
        // }
        // else
        // {
        // char[] columnName = new char[2];
        // columnName[0] = (char) ((a + 65) - 1);
        // columnName[1] = (char) (b + 65);
        //
        // return new String(columnName);
        // }
    }

    /**
     * Ergibt bei F5 = 6.
     *
     * @param cellName String
     *
     * @return int
     */
    public static int getColumnNumber(final String cellName)
    {
        char c1 = cellName.charAt(0);
        char c2 = cellName.charAt(1);

        int column = 0;

        if (((c2 >= (short) 'a') && (c2 <= (short) 'z')))
        {
            if (((c1 >= (short) 'a') && (c1 <= (short) 'z')))
            {
                column = getColumnNumber(c1 - 'a', c2 - 'a');
            }
            else
            {
                column = getColumnNumber(c1 - 'A', c2 - 'a');
            }
        }
        else if ((c2 >= 'A') && (c2 <= 'Z'))
        {
            if (((c1 >= 'a') && (c1 <= 'z')))
            {
                column = getColumnNumber(c1 - 'a', c2 - 'A');
            }
            else
            {
                column = getColumnNumber(c1 - 'A', c2 - 'A');
            }
        }
        else
        {
            if (((c1 >= 'a') && (c1 <= 'z')))
            {
                column = c1 - 'a';
            }
            else
            {
                column = c1 - 'A';
            }
        }

        return column;
    }

    /**
     * Ergibt ein String im Excel Format der selektierten Zellen.
     *
     * @param table {@link JTable}
     *
     * @return String
     */
    public static String getRange(final JTable table)
    {
        int[] selectedColumns = table.getSelectedColumns();
        int[] selectedRows = table.getSelectedRows();

        if ((selectedColumns.length > 0) && (selectedRows.length > 0))
        {
            String col1 = getColumnName(selectedColumns[0]);
            String col2 = getColumnName(selectedColumns[selectedColumns.length - 1]);
            String row1 = Integer.toString(selectedRows[0] + 1);
            String row2 = Integer.toString(selectedRows[selectedRows.length - 1] + 1);

            return String.format("%s%s:%s%s", col1, row1, col2, row2);
        }

        return null;
    }

    /**
     * Liefert die Anzahl der Zeilen/Spalten des Bereiches zurück.
     *
     * @param range String
     *
     * @return int
     *
     * @throws IllegalStateException Falls was schiefgeht.
     */
    public static int getRowColumnCount(final String range) throws IllegalStateException
    {
        StringTokenizer tokenizer = new StringTokenizer(range, ":");
        String start = tokenizer.nextToken();
        String end = tokenizer.nextToken();

        int rowStartNumber = getRowNumber(start);
        int rowEndNumber = getRowNumber(end);

        int columnStartNumber = getColumnNumber(start);
        int columnEndNumber = getColumnNumber(end);

        if (columnStartNumber == columnEndNumber)
        {
            // Eine Spalte selektiert, d.h. mehrere Zeilen
            return (rowEndNumber - rowStartNumber) + 1;
        }
        else if (rowEndNumber == rowStartNumber)
        {
            // Eine Zeile selektiert, d.h. mehrere Spalten
            return (columnEndNumber - columnStartNumber) + 1;
        }
        else
        {
            throw new IllegalStateException("Row-/Column regions are not supported");
        }
    }

    /**
     * Ergibt bei F5 = 5.
     *
     * @param cellName String
     *
     * @return int
     */
    public static int getRowNumber(final String cellName)
    {
        // char c1 = cellName.charAt(0);
        char c2 = cellName.charAt(1);

        int row = 0;

        if (((c2 >= 'a') && (c2 <= 'z')) || ((c2 >= 'A') && (c2 <= 'Z')))
        {
            String strRow = cellName.substring(2);
            row = Integer.parseInt(strRow);
        }
        else
        {
            String strRow = cellName.substring(1);
            row = Integer.parseInt(strRow);
        }

        return row - 1;
    }

    /**
     * Prüfen, ob der Excelbereich mehrere Rows/Columns enthält.
     *
     * @param value String
     *
     * @return boolean
     */
    public static boolean isMultiRowOrColumn(final String value)
    {
        StringTokenizer tokenizer = new StringTokenizer(value, ":");
        String start = tokenizer.nextToken();
        String end = tokenizer.nextToken();
        StringBuilder rowBuf = new StringBuilder();
        StringBuilder colBuf = new StringBuilder();

        for (int i = 0; i < start.length(); i++)
        {
            if (!Character.isDigit(start.charAt(i)))
            {
                colBuf.append(start.charAt(i));
            }
            else
            {
                rowBuf.append(start.charAt(i));
            }
        }

        String rowStart = rowBuf.toString();
        String colStart = colBuf.toString();
        rowBuf = new StringBuilder();
        colBuf = new StringBuilder();

        for (int i = 0; i < end.length(); i++)
        {
            if (!Character.isDigit(end.charAt(i)))
            {
                colBuf.append(end.charAt(i));
            }
            else
            {
                rowBuf.append(end.charAt(i));
            }
        }

        String rowEnd = rowBuf.toString();
        String colEnd = colBuf.toString();

        return (!rowStart.equals(rowEnd)) && (!colStart.equals(colEnd));
    }

    /**
     * Check to see if the range is too big.
     *
     * @param range String
     * @param numValues int
     *
     * @return boolean
     */
    public static boolean isRangeOk(final String range, final int numValues)
    {
        if ((range == null) || (range.indexOf(':') == -1))
        {
            return true;
        }

        StringTokenizer regionTokenizer = new StringTokenizer(range, ";");

        int numRangeValues = 0;

        while (regionTokenizer.hasMoreTokens())
        {
            StringTokenizer tokenizer = new StringTokenizer(regionTokenizer.nextToken(), ":");
            String start = (String) tokenizer.nextElement();
            String end = (String) tokenizer.nextElement();

            // Columns
            int startCol = getColumnNumber(start);
            int endCol = getColumnNumber(end);

            // Rows
            int startRow = getRowNumber(start);
            int endRow = getRowNumber(end);

            // Linear
            if (startCol == endCol)
            {
                numRangeValues += (endRow - startRow);
            }

            // Otherwise it's a block
            else
            {
                int numCols = endCol - startCol;
                int numRows = endRow - startRow;
                numRangeValues += (numCols * numRows);
            }
        }

        return numRangeValues <= numValues;
    }

    /**
     * Liefert true, wenn in einem Bereich von mehreren mit einem zu vergleichenden übereinstimmt.
     *
     * @param ranges String
     * @param range String
     *
     * @return boolean
     */
    public static boolean overlapAllRanges(final String ranges, final String range)
    {
        StringTokenizer tokenizer = new StringTokenizer(ranges, ";");

        while (tokenizer.hasMoreTokens())
        {
            if (overlapRanges(tokenizer.nextToken(), range))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Liefert true, wenn sich zwei Excelbereiche überschneiden.
     *
     * @param range1 String
     * @param range2 String
     *
     * @return boolean
     */
    public static boolean overlapRanges(final String range1, final String range2)
    {
        StringTokenizer tokenizer = new StringTokenizer(range1, ":");
        String start = tokenizer.nextToken();
        String end = tokenizer.nextToken();
        int r1FirstCol = getColumnNumber(start);
        int r1LastCol = getColumnNumber(end);
        int r1FirstRow = getRowNumber(start);
        int r1LastRow = getRowNumber(end);

        tokenizer = new StringTokenizer(range2, ":");
        start = tokenizer.nextToken();
        end = tokenizer.nextToken();

        int r2FirstCol = getColumnNumber(start);
        int r2LastCol = getColumnNumber(end);
        int r2FirstRow = getRowNumber(start);
        int r2LastRow = getRowNumber(end);

        Rectangle r1 = new Rectangle(r1FirstCol, r1FirstRow, (r1LastCol - r1FirstCol) + 1, (r1LastRow - r1FirstRow) + 1);
        Rectangle r2 = new Rectangle(r2FirstCol, r2FirstRow, (r2LastCol - r2FirstCol) + 1, (r2LastRow - r2FirstRow) + 1);

        return r1.intersects(r2);
    }

    /**
     * Calculate a column name that has two Characters.
     *
     * @param c1 int
     * @param c2 int
     *
     * @return int
     */
    private static int getColumnNumber(final int c1, final int c2)
    {
        // Bug Fix - Incorrect Columns referenced (15.08.2005)
        return ((c1 + 1) * 26) + c2;

        // return ((c1 + 1) * 25) + (c2 + 1);
    }

    /**
     * Erstellt ein neues {@link ExcelToolkit} Object.
     */
    private ExcelToolkit()
    {
        super();
    }
}
