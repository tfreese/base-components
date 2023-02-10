package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestObjectTable {
    @Test
    void testAdd() {
        ObjectTable objectTable = new ObjectTable(new String[]{"h1", "h2", "h3"});
        objectTable.addRow(List.of("d1", "d2", "d3"));
        objectTable.addRow(List.of("d1", "d2", "d3"));

        assertEquals(3, objectTable.getColumnCount());
        assertEquals(2, objectTable.getRowCount());
    }

    @Test
    void testAddTooMuchData() {
        ObjectTable objectTable = new ObjectTable(List.of("h1", "h2", "h3"));

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> objectTable.addRow(List.of("d1", "d2", "d3", "d4")));
    }

    @Test
    void testWriteCsv() {
        ObjectTable objectTable = new ObjectTable(List.of("h1", "h2", "h3"));
        objectTable.addRow(new String[]{"\"d1\"", null, "d3"});
        objectTable.addRow(List.of("d1", "d2", "d3"));

        objectTable.writeCsv(System.out);
    }

    @Test
    void testWriteStringTable() {
        ObjectTable objectTable = new ObjectTable(List.of("h1-llllllllll", "h2", "h3"));
        objectTable.addRow(Arrays.asList("\"d1\"", null, "d3"));
        objectTable.addRow(List.of("d1", "d2", "d3-llllllllll"));

        objectTable.writeStringTable(System.out, '=', '|');
    }
}
