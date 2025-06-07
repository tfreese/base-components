// Created: 13.09.2016
package de.freese.base.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
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
 * <li>headerRow = 0</li>
 * <li>firstDataRow = 1</li>
 * <li>fieldSeparator = ';'</li>
 * <li>quoteCharacter = '"'</li>
 * </ul>
 *
 * @author Thomas Freese
 */
public class ExcelToCsv {
    private final DataFormatter dataFormatter = new DataFormatter(Locale.getDefault(), true);
    /**
     * 0-Based
     */
    private BiFunction<Integer, String, String> columnValueConverter = (column, origin) -> origin;
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
    /**
     * 0-Based
     */
    private List<Integer> parseableColumns;
    private char quoteCharacter = '"';

    public void convert(final Path excelSource, final Path csvPath) throws IOException {
        Objects.requireNonNull(csvPath, "csvPath required");

        // StandardOpenOption.DELETE_ON_CLOSE
        try (BufferedWriter csvWriter = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
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

        Objects.requireNonNull(parseableColumns, "columnIndices required");

        if (parseableColumns.isEmpty()) {
            throw new IllegalArgumentException("columnIndices length is empty");
        }

        try (Workbook workbook = getWorkbook(excelSource)) {
            final Sheet sheet = workbook.getSheetAt(0);

            // Write Header.
            if (headerRow >= 0) {
                final String[] headers = getHeaders(sheet);
                writeCSV(csvWriter, headers);
            }

            // Read Data.
            // for (int r = firstDataRow; r < (sheet.getLastRowNum() + 1); r++)
            for (int r = firstDataRow; r < (sheet.getPhysicalNumberOfRows() + 1); r++) {
                final Row row = sheet.getRow(r);

                if (row == null) {
                    continue;
                }

                final String[] values = new String[parseableColumns.size()];

                for (int c = 0; c < parseableColumns.size(); c++) {
                    values[c] = getValue(row, parseableColumns.get(c));
                }

                // Do not write empty rows.
                if (Arrays.stream(values).filter(Objects::nonNull).anyMatch(s -> !s.isBlank())) {
                    writeCSV(csvWriter, values);
                }
            }

            csvWriter.flush();
        }
    }

    /**
     * {@link Function} for converting the value.<br>
     * Is called, when the Value != null and not empty.<br>
     */
    public void setColumnValueConverter(final BiFunction<Integer, String, String> columnValueConverter) {
        this.columnValueConverter = Objects.requireNonNull(columnValueConverter, "columnValueConverter required");
    }

    /**
     * Default: ';'
     */
    public void setFieldSeparator(final char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
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
     * If headerRow < 0 the Header s ignored.<br>
     */
    public void setHeaderRow(final int headerRow) {
        this.headerRow = headerRow;
    }

    /**
     * 0-Based
     */
    public void setParseableColumns(final List<Integer> parseableColumns) {
        Objects.requireNonNull(parseableColumns, "parseableColumns required");

        if (parseableColumns.isEmpty()) {
            throw new IllegalArgumentException("parseableColumns length is empty");
        }

        this.parseableColumns = List.copyOf(parseableColumns);
    }

    /**
     * Default: '"'
     */
    public void setQuoteCharacter(final char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Returns the Header or null, if headerLine < 0.
     */
    private String[] getHeaders(final Sheet sheet) {
        if (headerRow < 0) {
            return new String[0];
        }

        final Row row = sheet.getRow(headerRow);

        final String[] headers = new String[parseableColumns.size()];

        for (int c = 0; c < parseableColumns.size(); c++) {
            headers[c] = getValue(row, parseableColumns.get(c));
        }

        return headers;
    }

    private String getValue(final Row row, final int column) {
        final Cell cell = row.getCell(column);

        if (cell == null) {
            return null;
        }

        String value;

        if (!CellType.FORMULA.equals(cell.getCellType())) {
            value = dataFormatter.formatCellValue(cell);
        }
        else {
            // formulaEvaluator.evaluate(cell).getStringValue();
            value = dataFormatter.formatCellValue(cell, formulaEvaluator);
        }

        // if (CellType.STRING.equals(cell.getCellTypeEnum())) {
        // value = cell.getRichStringCellValue().getString();
        // }
        // else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
        // final double v = cell.getNumericCellValue();
        // value = Double.toString(v);
        // }

        // value = Optional.ofNullable(value).map(String::strip).orElse("");

        if (value.isBlank()) {
            value = null;
        }

        if (value != null && row.getRowNum() != headerRow) {
            value = columnValueConverter.apply(column, value);
        }

        return value;
    }

    private Workbook getWorkbook(final Path excelSource) throws IOException {
        final Workbook workbook;

        // workbook = WorkbookFactory.create(Files.newInputStream(excelSource, StandardOpenOption.READ));
        if (excelSource.toString().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        }
        else {
            workbook = new XSSFWorkbook(Files.newInputStream(excelSource, StandardOpenOption.READ));
            // formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }

        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        workbook.setMissingCellPolicy(MissingCellPolicy.RETURN_BLANK_AS_NULL);

        return workbook;
    }

    private void writeCSV(final Writer writer, final String[] values) throws IOException {
        final StringBuilder row = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            row.append(quoteCharacter).append(values[i]).append(quoteCharacter);

            if (i < (values.length - 1)) {
                row.append(fieldSeparator);
            }
        }

        row.append(System.lineSeparator());

        writer.write(row.toString());
    }
}
