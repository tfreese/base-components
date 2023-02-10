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
 * Convert a Excel-File in CSV-File.<br>
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
public class ExcelToCsv {
    /**
     * 0-Based
     */
    private final Map<Integer, Function<String, String>> columnFunctions = new HashMap<>();

    private final DataFormatter dataFormatter = new DataFormatter(Locale.getDefault(), true);
    /**
     * 0-Based
     */
    private int[] columnIndices;

    private char fieldSeparator = ';';
    /**
     * 0. Row = Header.
     */
    private int firstDataRow = 1;

    private FormulaEvaluator formulaEvaluator;
    /**
     * 0. Row = Header.
     */
    private int headerRow;

    private Character quoteCharacter = '"';

    public void convert(final Path excelSource, final Path csvDest) throws IOException {
        Objects.requireNonNull(csvDest, "csvDest required");

        // StandardOpenOption.DELETE_ON_CLOSE
        try (BufferedWriter csvWriter = Files.newBufferedWriter(csvDest, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
            convert(excelSource, csvWriter);
        }
    }

    public void convert(final Path excelSource, final Writer csvWriter) throws IOException {
        Objects.requireNonNull(excelSource, "excelSource required");
        Objects.requireNonNull(csvWriter, "csvWriter required");

        if (!Files.exists(excelSource)) {
            throw new IllegalArgumentException("excelSource not exist");
        }

        if (!Files.isReadable(excelSource)) {
            throw new IllegalArgumentException("excelSource not readable");
        }

        Objects.requireNonNull(this.columnIndices, "columnIndices required");

        if (this.columnIndices.length == 0) {
            throw new IllegalArgumentException("columnIndices length is " + this.columnIndices.length + "; expected >= 1");
        }

        try (Workbook workbook = getWorkbook(excelSource)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Write Header.
            if (this.headerRow >= 0) {
                String[] headers = getHeaders(sheet);
                writeCSV(csvWriter, headers);
            }

            // Read Data.
            // for (int r = this.firstDataRow; r < (sheet.getLastRowNum() + 1); r++)
            for (int r = this.firstDataRow; r < (sheet.getPhysicalNumberOfRows() + 1); r++) {
                Row row = sheet.getRow(r);

                if (row == null) {
                    continue;
                }

                String[] values = new String[this.columnIndices.length];

                for (int c = 0; c < this.columnIndices.length; c++) {
                    values[c] = getValue(row, this.columnIndices[c]);
                }

                // Do not write empty rows.
                if (Arrays.stream(values).filter(Objects::nonNull).anyMatch(s -> !s.isBlank())) {
                    writeCSV(csvWriter, values);
                }
            }

            csvWriter.flush();
        }
    }

    public void setColumnIndices(final int... columnIndices) {
        Objects.requireNonNull(columnIndices, "columnIndices required");

        if (columnIndices.length == 0) {
            throw new IllegalArgumentException("columnIndices length is " + columnIndices.length + "; expected >= 1");
        }

        this.columnIndices = columnIndices;
    }

    /**
     * {@link Function} for converting the value.<br>
     * Is called, when the Value != null and not empty.<br>
     */
    public void setConvertFunction(final int columnIndex, final Function<String, String> function) {
        this.columnFunctions.put(columnIndex, function);
    }

    /**
     * Default: ';'
     */
    public void setFieldSeparator(final char fieldSeparator) {
        this.fieldSeparator = Objects.requireNonNull(fieldSeparator, "fieldSeparator required");
    }

    /**
     * Default: 1 (0. Row = Header)
     */
    public void setFirstDataRow(final int firstDataRow) {
        if (firstDataRow < 0) {
            throw new IllegalArgumentException("firstDataRow is " + firstDataRow + "; expected >= 0");
        }

        this.firstDataRow = firstDataRow;
    }

    /**
     * Default: 0<br>
     * If < 0 the Header s ignored.<br>
     */
    public void setHeaderRow(final int headerRow) {
        this.headerRow = headerRow;
    }

    /**
     * Default: '"'
     */
    public void setQuoteCharacter(final Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Returns the Header or null, if headerLine < 0.
     */
    private String[] getHeaders(final Sheet sheet) {
        if (this.headerRow < 0) {
            return null;
        }

        Row row = sheet.getRow(this.headerRow);

        String[] headers = new String[this.columnIndices.length];

        for (int c = 0; c < this.columnIndices.length; c++) {
            headers[c] = getValue(row, this.columnIndices[c]);
        }

        return headers;
    }

    private String getValue(final Row row, final int column) {
        final Cell cell = row.getCell(column);

        if (cell == null) {
            return null;
        }

        String value = null;

        if (!CellType.FORMULA.equals(cell.getCellType())) {
            value = this.dataFormatter.formatCellValue(cell);
        }
        else {
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

        if (value.isBlank()) {
            value = null;
        }

        if ((value != null) && (row.getRowNum() != this.headerRow)) {
            // this.columnFunctions.getOrDefault(column, Function.identity()).apply(value);
            Function<String, String> function = this.columnFunctions.get(column);

            if (function != null) {
                value = function.apply(value);
            }
        }

        return value;
    }

    private Workbook getWorkbook(final Path excelSource) throws IOException {
        Workbook workbook = null;

        // workbook = WorkbookFactory.create(Files.newInputStream(excelSource, StandardOpenOption.READ));
        if (excelSource.toString().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        }
        else {
            workbook = new XSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }

        this.formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        workbook.setMissingCellPolicy(MissingCellPolicy.RETURN_BLANK_AS_NULL);

        return workbook;
    }

    private void writeCSV(final Writer writer, final String[] values) throws IOException {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (this.quoteCharacter != null) {
                row.append(this.quoteCharacter).append(values[i]).append(this.quoteCharacter);
            }
            else {
                row.append(values[i]);
            }

            if (i < (values.length - 1)) {
                row.append(this.fieldSeparator);
            }
        }

        // row.append("\n\r");
        row.append(System.lineSeparator());

        writer.write(row.toString());
    }
}
