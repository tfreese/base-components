// Created: 23.03.23
package de.freese.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.UnaryOperator;

/**
 * @author Thomas Freese
 */
public final class CsvUtils {

    /**
     * The {@link InputStream} is not closed.
     */
    public static List<String[]> parseCsv(final InputStream inputStream) {
        return parseCsv(new InputStreamReader(inputStream));
    }

    /**
     * The {@link Reader} is not closed.
     */
    public static List<String[]> parseCsv(final Reader reader) {
        final BufferedReader bufferedReader;

        if (reader instanceof BufferedReader br) {
            bufferedReader = br;
        }
        else {
            bufferedReader = new BufferedReader(reader);
        }

        return bufferedReader.lines()
                .filter(Objects::nonNull)
                .filter(line -> !line.strip().isBlank())
                .map(CsvUtils::parseCsvRow)
                .toList()
                ;
    }

    public static List<String[]> parseCsv(final Path path) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {
            return parseCsv(inputStream);
        }
    }

    /**
     * @param outputStream {@link OutputStream}; The Stream is not closed.
     * @param rowFinishPredicate {@link IntPredicate}; row -> true/false
     * @param headerFunction {@link IntFunction}; row -> value
     * @param valueFunction {@link BiFunction}; row, column -> value
     */
    public static void writeCsv(final OutputStream outputStream, final int columnCount, final IntPredicate rowFinishPredicate, final IntFunction<String> headerFunction,
                                final BiFunction<Integer, Integer, String> valueFunction) {
        final PrintStream printStream;

        if (outputStream instanceof PrintStream ps) {
            printStream = ps;
        }
        else {
            printStream = new PrintStream(outputStream, false, StandardCharsets.UTF_8);
        }

        final UnaryOperator<String> csvFormatFunction = value -> {
            if (value == null || value.strip().isBlank()) {
                return "";
            }

            String v = value;

            // Escape quotes.
            if (v.contains("\"")) {
                v = v.replace("\"", "\\\"\"");
            }

            // Escape comma.
            if (v.contains(",")) {
                v = v.replace(",", "\\,");
            }

            // Value in quotes.
            return "\"" + v + "\"";
        };

        // Header
        StringJoiner stringJoiner = new StringJoiner(",");

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            stringJoiner.add(csvFormatFunction.apply(headerFunction.apply(columnIndex)));
        }

        printStream.println(stringJoiner);

        // Data
        int rowIndex = 0;

        while (rowFinishPredicate.test(rowIndex)) {
            stringJoiner = new StringJoiner(",");

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                stringJoiner.add(csvFormatFunction.apply(valueFunction.apply(rowIndex, columnIndex)));
            }

            printStream.println(stringJoiner);
            rowIndex++;
        }

        printStream.flush();
    }

    private static String[] parseCsvRow(final String csvRow) {
        String row = csvRow;
        final List<String> token = new ArrayList<>();

        while (!row.isBlank()) {
            if (row.startsWith(",")) {
                // Empty Value
                token.add("");
                row = row.substring(1);
                continue;
            }

            final int endIndex = row.indexOf("\",");

            if (endIndex < 0) {
                // Last Value -> End
                token.add(row);
                break;
            }

            token.add(row.substring(0, endIndex + 1));
            row = row.substring(endIndex + 2);
        }

        return token.stream().map(t -> t.replaceAll("^\"|\"$", "")) // Remove first and last quote.
                .map(l -> l.replace("\\\"\"", "\"")) // Unescape quotes.
                .map(l -> l.replace("\\,", ",")) // Unescape comma.
                .map(String::strip).toArray(String[]::new);
    }

    private CsvUtils() {
        super();
    }
}
