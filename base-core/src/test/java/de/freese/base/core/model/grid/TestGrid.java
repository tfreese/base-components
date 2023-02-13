// Created: 25.01.2018
package de.freese.base.core.model.grid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.GenericGridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestGrid {
    @Test
    void testGrid() {
        Grid grid = new Grid();
        grid.addColumn(new BinaryGridColumn());
        grid.addColumn(new BooleanGridColumn());
        grid.addColumn(new DateGridColumn());
        grid.addColumn(new DoubleGridColumn());
        grid.addColumn(new IntegerGridColumn());
        grid.addColumn(new LongGridColumn());
        grid.addColumn(new StringGridColumn());

        GridRow row = GridRow.of(new Object[]{new byte[]{1, 2, 3}, true, new Date(), 1.23456D, 42, 123456L, "this is a test"});
        grid.addRow(row);

        assertNotNull(grid.getValue(Object.class, 0, 0));
        assertArrayEquals((byte[]) row.getObject(0), grid.getValue(byte[].class, 0, 0));

        assertNotNull(grid.getValue(Object.class, 0, 1));
        assertEquals(row.getObject(1), grid.getValue(Boolean.class, 0, 1));

        assertNotNull(grid.getValue(Object.class, 0, 2));
        assertEquals(row.getObject(2), grid.getValue(Date.class, 0, 2));

        assertNotNull(grid.getValue(Object.class, 0, 3));
        assertEquals(row.getObject(3), grid.getValue(Double.class, 0, 3));

        assertNotNull(grid.getValue(Object.class, 0, 4));
        assertEquals(row.getObject(4), grid.getValue(Integer.class, 0, 4));

        assertNotNull(grid.getValue(Object.class, 0, 5));
        assertEquals(row.getObject(5), grid.getValue(Long.class, 0, 5));

        assertNotNull(grid.getValue(Object.class, 0, 6));
        assertEquals(row.getObject(6), grid.getValue(String.class, 0, 6));
    }

    @Test
    void testGridColumnBinary() {
        GridColumn<byte[]> column = new BinaryGridColumn();

        assertEquals(byte[].class, column.getType());

        byte[] object = new byte[]{0, 1, 2, 3, 4, 5};

        assertNull(column.getValue(null));
        assertArrayEquals(object, column.getValue(object));
    }

    @Test
    void testGridColumnBoolean() {
        GridColumn<Boolean> column = new BooleanGridColumn();

        assertEquals(Boolean.class, column.getType());

        assertNull(column.getValue(null));
        assertFalse(column.getValue(false));
        assertTrue(column.getValue(true));
    }

    @Test
    void testGridColumnDate() {
        GridColumn<Date> column = new DateGridColumn();

        assertEquals(Date.class, column.getType());

        Date object = Date.from(ZonedDateTime.now().toInstant());
        // Date object = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(+2)));
        // Date object = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        // Date object = Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Berlin")).toInstant());

        assertNull(column.getValue(null));
        assertEquals(object, column.getValue(object));
    }

    @Test
    void testGridColumnDouble() {
        GridColumn<Double> column = new DoubleGridColumn();

        assertEquals(Double.class, column.getType());

        double value = 1.123456D;

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value), 0D);
    }

    @Test
    void testGridColumnGeneric() {
        ZoneId zoneId = ZoneId.systemDefault();
        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = zoneId.getRules().getOffset(instant);
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+2);

        // long time = Instant.now().getEpochSecond();
        // long time = value.atZone(zoneId).toInstant().toEpochMilli();
        // LocalDateTime value = LocalDateTime.ofEpochSecond(time, 0, zoneOffset);

        Function<Object, LocalDateTime> mapper = LocalDateTime.class::cast;

        GridColumn<LocalDateTime> column = new GenericGridColumn<>(LocalDateTime.class, mapper);

        assertEquals(LocalDateTime.class, column.getType());

        LocalDateTime value = LocalDateTime.now().withNano(0);

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));
    }

    @Test
    void testGridColumnInteger() {
        GridColumn<Integer> column = new IntegerGridColumn();

        assertEquals(Integer.class, column.getType());

        int value = 123456;

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));
    }

    @Test
    void testGridColumnLong() {
        GridColumn<Long> column = new LongGridColumn();

        assertEquals(Long.class, column.getType());

        long value = 123456L;

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));
    }

    @Test
    void testGridColumnString() {
        GridColumn<String> column = new StringGridColumn();

        assertEquals(String.class, column.getType());

        String value = ",.-öä\"#ü+";

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));
    }
}
