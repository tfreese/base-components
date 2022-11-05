// Created: 13.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Konvertiert eine Excel-Datei in eine CSV-Datei.<br>
 * <br>
 * Defaults:
 * <ul>
 * <li>headerRow = 0
 * <li>firstDataRow = 1
 * <li>fieldSeparator = ';'
 * <li>quoteCharacter = '"'
 * </ul>
 *
 * @author Thomas Freese
 */
public class ExcelToCsv
{
    /**
     * Beginnend mit 0.
     */
    private final Map<Integer, Function<String, String>> columnFunctions = new HashMap<>();
    /**
     *
     */
    private final DataFormatter dataFormatter = new DataFormatter(Locale.getDefault(), true);
    /**
     * Beginnend mit 0.
     */
    private int[] columnIndices;
    /**
     *
     */
    private char fieldSeparator = ';';
    /**
     * 0. Zeile = Header.
     */
    private int firstDataRow = 1;
    /**
     *
     */
    private FormulaEvaluator formulaEvaluator;
    /**
     * 0. Zeile = Header.
     */
    private int headerRow;
    /**
     *
     */
    private Character quoteCharacter = '"';

    public void convert(final Path excelSource, final Path csvDest) throws IOException
    {
        Objects.requireNonNull(csvDest, "csvDest required");

        // StandardOpenOption.DELETE_ON_CLOSE
        try (BufferedWriter csvWriter = Files.newBufferedWriter(csvDest, StandardCharsets.UTF_8, StandardOpenOption.WRITE))
        {
            convert(excelSource, csvWriter);
        }
    }

    public void convert(final Path excelSource, final Writer csvWriter) throws IOException
    {
        Objects.requireNonNull(excelSource, "excelSource required");
        Objects.requireNonNull(csvWriter, "csvWriter required");

        if (!Files.exists(excelSource))
        {
            throw new IllegalArgumentException("excelSource not exist");
        }

        if (!Files.isReadable(excelSource))
        {
            throw new IllegalArgumentException("excelSource not readable");
        }

        Objects.requireNonNull(this.columnIndices, "columnIndices required");

        if (this.columnIndices.length == 0)
        {
            throw new IllegalArgumentException("columnIndices length is " + this.columnIndices.length + "; expected >= 1");
        }

        try (Workbook workbook = getWorkbook(excelSource))
        {
            Sheet sheet = workbook.getSheetAt(0);

            // Header schreiben.
            if (this.headerRow >= 0)
            {
                String[] headers = getHeaders(sheet);
                writeCSV(csvWriter, headers);
            }

            // Daten auslesen.
            // for (int r = this.firstDataRow; r < (sheet.getLastRowNum() + 1); r++)
            for (int r = this.firstDataRow; r < (sheet.getPhysicalNumberOfRows() + 1); r++)
            {
                Row row = sheet.getRow(r);

                if (row == null)
                {
                    continue;
                }

                String[] values = new String[this.columnIndices.length];

                for (int c = 0; c < this.columnIndices.length; c++)
                {
                    values[c] = getValue(row, this.columnIndices[c]);
                }

                // Keine leeren Zeilen schreiben.
                if (Arrays.stream(values).filter(Objects::nonNull).anyMatch(s -> !s.isBlank()))
                {
                    writeCSV(csvWriter, values);
                }
            }

            csvWriter.flush();
        }
    }

    public void setColumnIndices(final int... columnIndices)
    {
        Objects.requireNonNull(columnIndices, "columnIndices required");

        if (columnIndices.length == 0)
        {
            throw new IllegalArgumentException("columnIndices length is " + columnIndices.length + "; expected >= 1");
        }

        this.columnIndices = columnIndices;
    }

    /**
     * Setzt das Trennzeichen der Datenfelder.<br>
     * Default: ';'
     */
    public void setFieldSeparator(final char fieldSeparator)
    {
        this.fieldSeparator = Objects.requireNonNull(fieldSeparator, "fieldSeparator required");
    }

    /**
     * Setzt die Zeile, in der die Daten beginnen.<br>
     * Default: 1 (0. Zeile = Header)
     */
    public void setFirstDataRow(final int firstDataRow)
    {
        if (firstDataRow < 0)
        {
            throw new IllegalArgumentException("firstDataRow is " + firstDataRow + "; expected >= 0");
        }

        this.firstDataRow = firstDataRow;
    }

    /**
     * Setzt die {@link Function} zum Formatieren des Spaltenwertes.<br>
     * Die Konvertierung-Funktion wird nur aufgerufen, wenn dass Value != null und nicht leer ist.<br>
     */
    public void setFunction(final int columnIndex, final Function<String, String> function)
    {
        this.columnFunctions.put(columnIndex, function);
    }

    /**
     * Setzt die Zeile des Headers.<br>
     * Default: 0<br>
     * Bei einem Wert < 0 wird der Header ignoriert.<br>
     */
    public void setHeaderRow(final int headerRow)
    {
        this.headerRow = headerRow;
    }

    /**
     * Setzt das Umschliessungs-Zeichen der Datenfelder.<br>
     * Default: '"'
     */
    public void setQuoteCharacter(final Character quoteCharacter)
    {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Liefert den Header oder null, wenn headerLine < 0.
     */
    private String[] getHeaders(final Sheet sheet)
    {
        if (this.headerRow < 0)
        {
            return null;
        }

        Row row = sheet.getRow(this.headerRow);

        String[] headers = new String[this.columnIndices.length];

        for (int c = 0; c < this.columnIndices.length; c++)
        {
            headers[c] = getValue(row, this.columnIndices[c]);
        }

        return headers;
    }

    /**
     * Diese Methode holt sich den Wert aus der Zelle.
     */
    private String getValue(final Row row, final int column)
    {
        final Cell cell = row.getCell(column);

        if (cell == null)
        {
            return null;
        }

        String value = null;

        if (!CellType.FORMULA.equals(cell.getCellType()))
        {
            value = this.dataFormatter.formatCellValue(cell);
        }
        else
        {
            // this.formulaEvaluator.evaluate(cell).getStringValue();
            value = this.dataFormatter.formatCellValue(cell, this.formulaEvaluator);
        }

        // if (CellType.STRING.equals(cell.getCellTypeEnum()))
        // {
        // value = cell.getRichStringCellValue().getString();
        // }
        // else if (CellType.NUMERIC.equals(cell.getCellTypeEnum()))
        // {
        // final double v = cell.getNumericCellValue();
        // value = Double.toString(v);
        // }

        value = Optional.ofNullable(value).map(String::strip).orElse("");

        if (value.isBlank())
        {
            value = null;
        }

        if ((value != null) && (row.getRowNum() != this.headerRow))
        {
            // this.columnFunctions.getOrDefault(column, Function.identity()).apply(value);
            Function<String, String> function = this.columnFunctions.get(column);

            if (function != null)
            {
                value = function.apply(value);
            }
        }

        return value;
    }

    private Workbook getWorkbook(final Path excelSource) throws IOException
    {
        Workbook workbook = null;

        // workbook = WorkbookFactory.create(Files.newInputStream(excelSource, StandardOpenOption.READ));
        if (excelSource.toString().toLowerCase().endsWith(".xls"))
        {
            workbook = new HSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        }
        else
        {
            workbook = new XSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }

        this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        workbook.setMissingCellPolicy(MissingCellPolicy.RETURN_BLANK_AS_NULL);

        return workbook;
    }

    /**
     * Schreibt die Daten in die CSV-Datei.
     */
    private void writeCSV(final Writer writer, final String[] values) throws IOException
    {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < values.length; i++)
        {
            if (this.quoteCharacter != null)
            {
                row.append(this.quoteCharacter).append(values[i]).append(this.quoteCharacter);
            }
            else
            {
                row.append(values[i]);
            }

            if (i < (values.length - 1))
            {
                row.append(this.fieldSeparator);
            }
        }

        // row.append("\n\r");
        row.append(System.lineSeparator());

        writer.write(row.toString());
    }
}
