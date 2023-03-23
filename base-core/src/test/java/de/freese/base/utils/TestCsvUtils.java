package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCsvUtils {

    @Test
    void testParseCsv() throws Exception {
        String csv = """
                "Header_0","Header_1","Header_2"
                "Value\\""\\,_0_0","Value\\""\\,_0_1","Value\\""\\,_0_2"
                "Value\\""\\,_1_0","Value\\""\\,_1_1","Value\\""\\,_1_2"
                "Value\\""\\,_2_0","Value\\""\\,_2_1","Value\\""\\,_2_2"
                """;

        StringReader stringReader = new StringReader(csv);

        List<String[]> data = CsvUtils.parseCsv(stringReader);

        assertEquals("[Header_0, Header_1, Header_2]", Arrays.toString(data.get(0)));
        assertEquals("[Value\",_0_0, Value\",_0_1, Value\",_0_2]", Arrays.toString(data.get(1)));
        assertEquals("[Value\",_1_0, Value\",_1_1, Value\",_1_2]", Arrays.toString(data.get(2)));
        assertEquals("[Value\",_2_0, Value\",_2_1, Value\",_2_2]", Arrays.toString(data.get(3)));
    }

    @Test
    void testWriteCsv() throws Exception {
        IntFunction<String> headerFunction = column -> "Header_" + column;
        BiFunction<Integer, Integer, String> dataFunction = (row, column) -> "Value\",_" + row + "_" + column;
        IntPredicate finishPredicate = row -> row < 3;

        //        CsvUtils.writeCsv(System.out, 3, headerFunction, dataFunction, finishPredicate);

        byte[] data = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            CsvUtils.writeCsv(baos, 3, headerFunction, dataFunction, finishPredicate);

            data = baos.toByteArray();
        }

        String expected = """
                "Header_0","Header_1","Header_2"
                "Value\\""\\,_0_0","Value\\""\\,_0_1","Value\\""\\,_0_2"
                "Value\\""\\,_1_0","Value\\""\\,_1_1","Value\\""\\,_1_2"
                "Value\\""\\,_2_0","Value\\""\\,_2_1","Value\\""\\,_2_2"
                """;

        assertEquals(expected, new String(data, StandardCharsets.UTF_8));

        //        try (OutputStream outputStream = Files.newOutputStream(Paths.get(System.getProperty("java.io.tmpdir"), "csv-test.csv"), StandardOpenOption.TRUNCATE_EXISTING)) {
        //            CsvUtils.writeCsv(outputStream, 3, headerFunction, dataFunction, finishPredicate);
        //        }
    }
}
