// Created: 18.04.2020
package de.freese.base.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * @author Thomas Freese
 */
public final class ObjectTable {
    private final List<Object[]> data;
    private final List<String> header;

    public ObjectTable(final String[] header) {
        this(Arrays.asList(header));
    }

    public ObjectTable(final Iterable<String> header) {
        super();

        Objects.requireNonNull(header, "header required");

        this.header = StreamSupport.stream(header.spliterator(), false).toList();

        if (this.header.isEmpty()) {
            throw new IllegalArgumentException("header is empty");
        }

        this.data = new ArrayList<>();
    }

    /**
     * Die Row muss die gleiche Anzahl an Daten haben, wie die Anzahl der Spalten, sonst knallt es.
     */
    public void addRow(final Object[] row) {
        addRow(Arrays.asList(row));
    }

    /**
     * Die Row muss die gleiche Anzahl an Daten haben, wie die Anzahl der Spalten, sonst knallt es.
     */
    public void addRow(final Iterable<Object> row) {
        final Object[] rowData = new Object[getColumnCount()];
        int column = 0;

        for (Object value : row) {
            rowData[column] = value;
            column++;
        }

        data.add(rowData);
    }

    public int getColumnCount() {
        return header.size();
    }

    public List<Object[]> getData() {
        return List.copyOf(this.data);
    }

    public List<String> getHeader() {
        return List.copyOf(this.header);
    }

    public int getRowCount() {
        return data.size();
    }

    /**
     * Schreibt Header und Daten in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     */
    public void writeCsv(final OutputStream outputStream) {
        final IntFunction<String> headerFunction = this.header::get;
        final BiFunction<Integer, Integer, String> dataFunction = (row, column) -> Objects.toString(data.get(row)[column], null);
        final IntPredicate finishPredicate = row -> row < getRowCount();

        CsvUtils.writeCsv(outputStream, getColumnCount(), headerFunction, dataFunction, finishPredicate);
    }

    /**
     * Schreibt Header und Daten in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     */
    public void writeStringTable(final OutputStream outputStream, final char separatorHeader, final char separatorData) {
        writeStringTable(outputStream, separatorHeader, separatorData, value -> Objects.toString(value, ""));
    }

    /**
     * Schreibt Header und Daten in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     */
    public void writeStringTable(final OutputStream outputStream, final char separatorHeader, final char separatorData, final Function<Object, String> dataFunction) {
        final PrintStream printStream = outputStream instanceof PrintStream ps ? ps : new PrintStream(outputStream);

        final int[] columnWidths = new int[getColumnCount()];
        Arrays.fill(columnWidths, 0);

        IntStream.range(0, columnWidths.length).forEach(column -> {
            final int lengthHeader = Objects.toString(header.get(column), "").length();
            final int lengthData = data.stream().map(row -> row[column]).map(dataFunction).filter(Objects::nonNull).mapToInt(CharSequence::length).max().orElse(0);

            columnWidths[column] = Math.max(lengthHeader, lengthData);
        });

        final String headerString = IntStream.range(0, columnWidths.length).mapToObj(column -> {
            final String h = header.get(column);
            return h + " ".repeat(columnWidths[column] - h.length());
        }).collect(Collectors.joining(" " + separatorData + " "));

        printStream.println(headerString);
        printStream.println(("" + separatorHeader).repeat(headerString.length()));

        data.forEach(row -> {
            final String rowString = IntStream.range(0, columnWidths.length).mapToObj(column -> {
                final String value = dataFunction.apply(row[column]);
                return value + " ".repeat(columnWidths[column] - value.length());
            }).collect(Collectors.joining(" " + separatorData + " "));

            printStream.println(rowString);
        });

        printStream.flush();
    }
}
