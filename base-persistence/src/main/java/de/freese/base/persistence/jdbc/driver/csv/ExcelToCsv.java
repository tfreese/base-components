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
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
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
     * Beginnend mit 0.
     */
    private int[] columnIndicies = null;

    /**
     *
     */
    private final DataFormatter dataFormatter = new DataFormatter(Locale.getDefault(), true);

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
    private FormulaEvaluator formulaEvaluator = null;

    /**
     * 0. Zeile = Header.
     */
    private int headerRow = 0;

    /**
     *
     */
    private Character quoteCharacter = '"';

    /**
     * Erzeugt eine neue Instanz von {@link ExcelToCsv}
     */
    public ExcelToCsv()
    {
        super();
    }

    /**
     * Konvertiert eine Excel-Datei in eine CSV-Datei.
     *
     * @param excelSource {@link Path}
     * @param csvDest {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public void convert(final Path excelSource, final Path csvDest) throws IOException
    {
        Objects.requireNonNull(csvDest, "csvDest required");

        // StandardOpenOption.DELETE_ON_CLOSE
        try (BufferedWriter csvWriter = Files.newBufferedWriter(csvDest, StandardCharsets.UTF_8, StandardOpenOption.WRITE))
        {
            convert(excelSource, csvWriter);
        }
    }

    /**
     * Konvertiert eine Excel-Datei in eine CSV-Datei.
     *
     * @param excelSource {@link Path}
     * @param csvWriter {@link Writer}
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
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

        Objects.requireNonNull(this.columnIndicies, "columnIndicies required");

        if (this.columnIndicies.length == 0)
        {
            throw new IllegalArgumentException("columnIndicies length is " + this.columnIndicies.length + "; expected >= 1");
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

                String[] values = new String[this.columnIndicies.length];

                for (int c = 0; c < this.columnIndicies.length; c++)
                {
                    values[c] = getValue(row, this.columnIndicies[c]);
                }

                // Keine leeren Zeilen schreiben.
                if (Arrays.stream(values).filter(StringUtils::isNotBlank).count() > 0)
                {
                    writeCSV(csvWriter, values);
                }
            }

            csvWriter.flush();
        }
    }

    /**
     * Liefert den Header oder null, wenn headerLine < 0.
     *
     * @param sheet {@link Sheet}
     * @return String[]
     */
    private String[] getHeaders(final Sheet sheet)
    {
        if (this.headerRow < 0)
        {
            return null;
        }

        Row row = sheet.getRow(this.headerRow);

        String[] headers = new String[this.columnIndicies.length];

        for (int c = 0; c < this.columnIndicies.length; c++)
        {
            headers[c] = getValue(row, this.columnIndicies[c]);
        }

        return headers;
    }

    /**
     * Diese Methode holt sich den Wert aus der Zelle.
     *
     * @param row - {@link Row}
     * @param column - int
     * @return value - {@link String}
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

        value = StringUtils.trimToNull(value);

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

    /**
     * @param excelSource {@link Path}
     * @return {@link Workbook}
     * @throws IOException Falls was schief geht.
     */
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
     * @param columnIndicies int[]
     */
    public void setColumnIndicies(final int...columnIndicies)
    {
        Objects.requireNonNull(columnIndicies, "columnIndicies required");

        if (columnIndicies.length == 0)
        {
            throw new IllegalArgumentException("columnIndicies length is " + columnIndicies.length + "; expected >= 1");
        }

        this.columnIndicies = columnIndicies;
    }

    /**
     * Setzt das Trennzeichen der Datenfelder.<br>
     * Default: ';'
     *
     * @param fieldSeparator char
     */
    public void setFieldSeparator(final char fieldSeparator)
    {
        this.fieldSeparator = Objects.requireNonNull(fieldSeparator, "fieldSeparator required");
    }

    /**
     * Setzt die Zeile in der die Daten beginnen.<br>
     * Default: 1 (0. Zeile = Header)
     *
     * @param firstDataRow int
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
     * Setzt die {@link Function} zum formatieren des Spaltenwertes.<br>
     * Die Konvertierungs-Funktion wird nur aufgerufen, wenn das Value != null und nicht leer ist.<br>
     *
     * @param columnIndex int
     * @param function {@link Function}
     */
    public void setFunction(final int columnIndex, final Function<String, String> function)
    {
        this.columnFunctions.put(columnIndex, function);
    }

    /**
     * Setzt die Zeile des Headers.<br>
     * Default: 0<br>
     * Bei einem Wert < 0 wird der Header ignoriert.<br>
     *
     * @param headerRow int
     */
    public void setHeaderRow(final int headerRow)
    {
        this.headerRow = headerRow;
    }

    /**
     * Setzt das Umschliessungszeichen der Datenfelder.<br>
     * Default: '"'
     *
     * @param quoteCharacter {@link Character}
     */
    public void setQuoteCharacter(final Character quoteCharacter)
    {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Schreibt die Daten in die CSV-Datei.
     *
     * @param writer {@link Writer}
     * @param values String[]
     * @throws IOException Falls was schief geht.
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
