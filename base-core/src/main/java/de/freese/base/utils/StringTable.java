// Created: 18.04.2020
package de.freese.base.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Thomas Freese
 */
public final class StringTable
{
    /**
     *
     */
    private final List<String[]> data;
    /**
     *
     */
    private final String[] header;

    /**
     * Erstellt ein neues {@link StringTable} Object.
     *
     * @param header String[]
     * @param data List<String[]>
     */
    public StringTable(final String[] header, final List<String[]> data)
    {
        super();

        if (ArrayUtils.isNotEmpty(header) && ListUtils.isNotEmpty(data))
        {
            if (header.length != data.get(0).length)
            {
                throw new IllegalArgumentException("header length != data length");
            }
        }

        this.header = header;
        this.data = data;
    }

    /**
     * Escaped den Header und die Daten.
     *
     * @param escape char
     */
    public void escape(final char escape)
    {
        StringUtils.escape(getHeader(), escape);
        StringUtils.escape(getData(), escape);
    }

    /**
     * @return int
     */
    public int getColumnCount()
    {
        int columnCount = 0;

        if (ArrayUtils.isNotEmpty(getHeader()))
        {
            columnCount = getHeader().length;
        }
        else if (ListUtils.isNotEmpty(getData()))
        {
            columnCount = getData().get(0).length;
        }

        return columnCount;
    }

    /**
     * @return int[]
     */
    public int[] getColumnWidths()
    {
        int[] columnWidthsHeader = StringUtils.getWidths(getHeader());
        int[] columnWidthsData = StringUtils.getWidths(getData());

        int[] columnWidths = new int[getColumnCount()];

        for (int column = 0; column < columnWidths.length; column++)
        {
            columnWidths[column] = Math.max(columnWidthsHeader[column], columnWidthsData[column]);
        }

        return columnWidths;
    }

    /**
     * @return List<String[]>
     */
    public List<String[]> getData()
    {
        return this.data;
    }

    /**
     * @return String[]
     */
    public String[] getHeader()
    {
        return this.header;
    }

    /**
     * Die Spaltenbreite der Elemente wird auf den breitesten Wert durch das Padding aufgefüllt.<br>
     * Ist das Padding null oder leer wird nichts gemacht.<br>
     *
     * @param padding String
     */
    public void rightpad(final String padding)
    {
        if ((ArrayUtils.isEmpty(getHeader()) && ListUtils.isEmpty(getData())) || StringUtils.isEmpty(padding))
        {
            return;
        }

        int[] columnWidths = getColumnWidths();

        // Strings pro Spalte formatieren und schreiben.
        StringUtils.rightpad(getHeader(), columnWidths, padding);
        StringUtils.rightpad(getData(), columnWidths, padding);
    }

    /**
     * Schreibt die Liste in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     *
     * @param printStream {@link PrintStream}
     * @param separatorHeader String; horizontaler Separator zwischen Header und Daten, optional
     * @param separatorData String; vertikaler Separator zwischen Spalten
     */
    public void write(final PrintStream printStream, final String separatorHeader, final String separatorData)
    {
        if ((ArrayUtils.isEmpty(getHeader()) && ListUtils.isEmpty(getData())))
        {
            return;
        }

        if (ArrayUtils.isNotEmpty(getHeader()))
        {
            String headerSeparator = null;

            if (StringUtils.isNotEmpty(separatorHeader))
            {
                int width = Stream.of(getHeader()).mapToInt(CharSequence::length).sum();
                width += (getHeader().length - 1) * separatorData.length();

                headerSeparator = StringUtils.repeat(separatorHeader, width);
            }

            if (StringUtils.isNotEmpty(headerSeparator))
            {
                printStream.println(headerSeparator);
            }

            StringUtils.write(getHeader(), printStream, separatorData);

            if (StringUtils.isNotEmpty(headerSeparator))
            {
                printStream.println(headerSeparator);
            }
        }

        // parallel() verfälscht die Reihenfolge.
        StringUtils.write(getData(), printStream, separatorData);

        printStream.flush();
    }
}
