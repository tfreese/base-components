// Created: 25.01.2018
package de.freese.base.core.model.grid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.freese.base.core.model.grid.builder.GridBuilder;
import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(MockitoExtension.class)

// Sonst müsste pro Test-Methode der Mock als Parameter definiert und konfiguriert werden.
@MockitoSettings(strictness = Strictness.LENIENT)
class TestGrid {
    @Test
    void testGridBuilder() {
        //@formatter:off
       Grid grid = GridBuilder.create()
               .column(new IntegerGridColumn("int"))
               .column(Long.class)
                   .name("long")
                   .comment("long-column")
               .and()
               .column(Double.class)
                   .name("double")
                   .comment("double-column")
               .build();
       //@formatter:on

        assertEquals(3, grid.getColumnCount());
        assertEquals(0, grid.getRowCount());

        assertEquals("int", grid.getName(0));
        assertEquals("long", grid.getName(1));
        assertEquals("double", grid.getName(2));

        grid.addColumn(new BooleanGridColumn("boolean"));

        assertEquals(4, grid.getColumnCount());
        assertEquals("int", grid.getName(0));
        assertEquals("long", grid.getName(1));
        assertEquals("double", grid.getName(2));
        assertEquals("boolean", grid.getName(3));

        GridColumn<?> column = grid.removeColumn(1);
        assertNotNull(column);
        assertEquals("long", column.getName());

        assertEquals("int", grid.getName(0));
        assertEquals("double", grid.getName(1));
        assertEquals("boolean", grid.getName(2));
    }

    @Test
    void testGridMetaSave() throws IOException, ClassNotFoundException {
        //@formatter:off
        Grid grid = GridBuilder.create()
                .column(byte[].class)
                .and()
                .column(Boolean.class)
                    .name("boolean")
                    .comment("test-boolean")
                .and()
                .column(Date.class)
                    .name("Date")
                    .comment("test-date")
                 .and()
                 .column(Double.class)
                     .name("double")
                     .comment("test-double")
                 .and()
                 .column(Integer.class)
                 .and()
                 .column(Long.class)
                     .length(13)
                     .precision(42)
                 .and()
                 .column(String.class)
                 .build();
        //@formatter:on

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos)) {
            grid.write(dos);
        }

        byte[] bytes = baos.toByteArray();

        GridMetaData gmd2 = new GridMetaData();

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes))) {
            gmd2.readMetaData(dis);
        }

        assertEquals(7, grid.getColumnCount());
        assertEquals(grid.getColumnCount(), gmd2.getColumnCount());

        for (int i = 0; i < grid.getColumnCount(); i++) {
            assertEquals(grid.getType(i), gmd2.getColumn(i).getType());
            assertEquals(grid.getName(i), gmd2.getColumn(i).getName());
            assertEquals(grid.getComment(i), gmd2.getColumn(i).getComment());
            assertEquals(grid.getLength(i), gmd2.getColumn(i).getLength());
            assertEquals(grid.getPrecision(i), gmd2.getColumn(i).getPrecision());
        }
    }

    @Test
    void testGridResultSet() throws SQLException {
        Object[][] data = new Object[][]{{true, 1.23456D, "nur ein test"}, {false, 654321D, "-"}};

        ResultSetMetaData metaDataMock = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metaDataMock.getColumnCount()).then(invocation -> 3);

        Mockito.when(metaDataMock.getColumnType(1)).then(invocation -> Types.BOOLEAN);
        Mockito.when(metaDataMock.getColumnLabel(1)).then(invocation -> "boolean");
        Mockito.when(metaDataMock.getColumnDisplaySize(1)).then(invocation -> 1);
        Mockito.when(metaDataMock.getPrecision(1)).then(invocation -> 1);

        Mockito.when(metaDataMock.getColumnType(2)).thenReturn(Types.DOUBLE);
        Mockito.when(metaDataMock.getColumnLabel(2)).thenReturn("double");
        Mockito.when(metaDataMock.getColumnDisplaySize(2)).thenReturn(3);
        Mockito.when(metaDataMock.getPrecision(2)).thenReturn(3);

        Mockito.when(metaDataMock.getColumnType(3)).thenReturn(Types.VARCHAR);
        Mockito.when(metaDataMock.getColumnLabel(3)).thenReturn("string");
        Mockito.when(metaDataMock.getColumnDisplaySize(3)).thenReturn(10);
        Mockito.when(metaDataMock.getPrecision(3)).thenReturn(10);

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMock.getMetaData()).thenReturn(metaDataMock);

        AtomicInteger resultSetIndex = new AtomicInteger(-1);
        Mockito.when(resultSetMock.next()).then(invocation -> {
            resultSetIndex.getAndIncrement();
            return resultSetIndex.get() < data.length;
        });

        when(resultSetMock.getObject(1)).then(invocation -> data[resultSetIndex.get()][0]);
        when(resultSetMock.getObject(2)).then(invocation -> data[resultSetIndex.get()][1]);
        when(resultSetMock.getObject(3)).then(invocation -> data[resultSetIndex.get()][2]);

        Grid grid = new Grid();
        grid.read(resultSetMock);

        assertEquals(3, grid.getColumnCount());
        assertEquals(data.length, grid.getRowCount());

        assertEquals(Boolean.class, grid.getType(0));
        assertEquals("boolean", grid.getName(0));
        assertNull(grid.getComment(0));
        assertEquals(1, grid.getLength(0));
        assertEquals(1, grid.getPrecision(0));

        assertEquals(Double.class, grid.getType(1));
        assertEquals("double", grid.getName(1));
        assertNull(grid.getComment(1));
        assertEquals(3, grid.getLength(1));
        assertEquals(3, grid.getPrecision(1));

        assertEquals(String.class, grid.getType(2));
        assertEquals("string", grid.getName(2));
        assertNull(grid.getComment(2));
        assertEquals(10, grid.getLength(2));
        assertEquals(10, grid.getPrecision(2));

        for (int r = 0; r < grid.getRowCount(); r++) {
            for (int c = 1; c < grid.getColumnCount(); c++) {

                assertEquals(data[r][c], grid.getValue(Object.class, c, r));
            }
        }
    }

    @Test
    void testGridSave() throws IOException, ClassNotFoundException {
        Grid grid1 = new Grid();
        grid1.addColumn(new BinaryGridColumn());
        grid1.addColumn(new BooleanGridColumn());
        grid1.addColumn(new DateGridColumn());
        grid1.addColumn(new DoubleGridColumn());
        grid1.addColumn(new IntegerGridColumn());
        grid1.addColumn(new LongGridColumn());
        grid1.addColumn(new StringGridColumn());

        Object[] row1 = new Object[]{new byte[]{1, 2, 3}, false, new Date(), 1.23456D, 42, 123456L, "dies ist ein test"};
        grid1.addRow(row1);

        Object[] row2 = new Object[]{new byte[]{4, 5, 6}, true, new Date(System.currentTimeMillis() - 200000), 6.54321D, 24, 654321L, ",.-#äöü+"};
        grid1.addRow(row2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos)) {
            grid1.write(dos);
        }

        byte[] bytes = baos.toByteArray();

        Grid grid2 = new Grid(new GridMetaData());

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes))) {
            grid2.read(dis);
        }

        assertEquals(7, grid1.getColumnCount());
        assertEquals(2, grid1.getRowCount());
        assertEquals(grid1.getColumnCount(), grid2.getColumnCount());
        assertEquals(grid1.getRowCount(), grid2.getRowCount());

        for (int i = 0; i < grid1.getColumnCount(); i++) {
            assertEquals(grid1.getType(i), grid2.getType(i));
            assertEquals(grid1.getName(i), grid2.getName(i));
            assertEquals(grid1.getComment(i), grid2.getComment(i));
            assertEquals(grid1.getLength(i), grid2.getLength(i));
            assertEquals(grid1.getPrecision(i), grid2.getPrecision(i));
        }

        for (int r = 0; r < grid1.getRowCount(); r++) {
            byte[] a1 = grid1.getValue(byte[].class, 0, r);
            byte[] a2 = grid2.getValue(byte[].class, 0, r);

            assertArrayEquals(a1, a2);

            for (int c = 1; c < grid1.getColumnCount(); c++) {
                Object value1 = grid1.getValue(Object.class, c, r);
                Object value2 = grid2.getValue(Object.class, c, r);

                assertEquals(value1, value2);
            }
        }
    }
}
