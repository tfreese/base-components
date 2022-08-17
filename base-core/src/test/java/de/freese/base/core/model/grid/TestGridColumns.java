// Created: 25.01.2018
package de.freese.base.core.model.grid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;

import de.freese.base.core.function.ThrowingBiConsumer;
import de.freese.base.core.function.ThrowingFunction;
import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.GenericGridColumn;
import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestGridColumns
{
    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testBinary() throws IOException
    {
        BinaryGridColumn column = new BinaryGridColumn();

        assertEquals(byte[].class, column.getObjectClazz());

        byte[] object = new byte[]
                {
                        0, 1, 2, 3, 4, 5
                };

        assertNull(column.getValue(null));
        assertArrayEquals(object, column.getValue(object));

        assertNull(read(column, write(column, null)));
        assertArrayEquals(object, read(column, write(column, object)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testBoolean() throws IOException
    {
        BooleanGridColumn column = new BooleanGridColumn();

        assertEquals(Boolean.class, column.getObjectClazz());

        assertNull(column.getValue(null));
        assertFalse(column.getValue(false));
        assertTrue(column.getValue(true));

        assertNull(read(column, write(column, null)));
        assertFalse(read(column, write(column, false)));
        assertTrue(read(column, write(column, true)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testDate() throws IOException
    {
        DateGridColumn column = new DateGridColumn();

        assertEquals(Date.class, column.getObjectClazz());

        Date object = Date.from(ZonedDateTime.now().toInstant());
        // Date object = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(+2)));
        // Date object = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        // Date object = Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Berlin")).toInstant());

        assertNull(column.getValue(null));
        assertEquals(object, column.getValue(object));

        assertNull(read(column, write(column, null)));
        assertEquals(object, read(column, write(column, object)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testDouble() throws IOException
    {
        DoubleGridColumn column = new DoubleGridColumn();

        assertEquals(Double.class, column.getObjectClazz());

        double value = 1.123456D;

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value), 0D);

        assertNull(read(column, write(column, null)));
        assertEquals(value, read(column, write(column, value)), 0D);
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testGeneric() throws IOException
    {
        ZoneId zoneId = ZoneId.systemDefault();
        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = zoneId.getRules().getOffset(instant);
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+2);

        // long time = Instant.now().getEpochSecond();
        // long time = value.atZone(zoneId).toInstant().toEpochMilli();
        // LocalDateTime value = LocalDateTime.ofEpochSecond(time, 0, zoneOffset);

        Function<Object, LocalDateTime> mapper = obj -> (LocalDateTime) obj;

        ThrowingBiConsumer<DataOutput, LocalDateTime, IOException> writer = (dataOutput, value) ->
        {
            long time = value.atZone(zoneId).toInstant().toEpochMilli();

            dataOutput.writeLong(time);
        };

        ThrowingFunction<DataInput, LocalDateTime, IOException> reader = dataInput ->
        {
            long time = dataInput.readLong();

            Instant instant = Instant.ofEpochMilli(time);
            LocalDateTime value = LocalDateTime.ofInstant(instant, zoneId);

            return value;
        };

        GenericGridColumn<LocalDateTime> column = new GenericGridColumn<>(LocalDateTime.class, mapper, writer, reader);

        assertEquals(LocalDateTime.class, column.getObjectClazz());

        LocalDateTime value = LocalDateTime.now().withNano(0);

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));

        assertNull(read(column, write(column, null)));
        assertEquals(value, read(column, write(column, value)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testInteger() throws IOException
    {
        IntegerGridColumn column = new IntegerGridColumn();

        assertEquals(Integer.class, column.getObjectClazz());

        int value = 123456;

        assertNull(column.getValue(null));
        assertEquals((Integer) value, column.getValue(value));

        assertNull(read(column, write(column, null)));
        assertEquals((Integer) value, read(column, write(column, value)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testLong() throws IOException
    {
        LongGridColumn column = new LongGridColumn();

        assertEquals(Long.class, column.getObjectClazz());

        long value = 123456L;

        assertNull(column.getValue(null));
        assertEquals((Long) value, column.getValue(value));

        assertNull(read(column, write(column, null)));
        assertEquals((Long) value, read(column, write(column, value)));
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    @Test
    void testString() throws IOException
    {
        StringGridColumn column = new StringGridColumn();

        assertEquals(String.class, column.getObjectClazz());

        String value = ",.-öä\"#ü+";

        assertNull(column.getValue(null));
        assertEquals(value, column.getValue(value));

        assertNull(read(column, write(column, null)));
        assertEquals(value, read(column, write(column, value)));
    }

    /**
     * @param column {@link GridColumn}
     * @param bytes byte[]
     *
     * @return Object
     *
     * @throws IOException Falls was schiefgeht.
     */
    private <T> T read(final GridColumn<T> column, final byte[] bytes) throws IOException
    {
        T value = null;

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes)))
        {
            value = column.read(dis);
        }

        return value;
    }

    /**
     * @param column {@link GridColumn}
     * @param object Object
     *
     * @return byte[]
     *
     * @throws IOException Falls was schiefgeht.
     */
    private <T> byte[] write(final GridColumn<T> column, final Object object) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos))
        {
            column.write(dos, object);
        }

        byte[] bytes = baos.toByteArray();

        return bytes;
    }
}
