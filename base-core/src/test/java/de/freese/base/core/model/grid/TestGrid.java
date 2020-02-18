// Created: 25.01.2018
package de.freese.base.core.model.grid;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import de.freese.base.core.model.grid.column.AbstractGridColumn;
import de.freese.base.core.model.grid.column.BinaryGridColumn;
import de.freese.base.core.model.grid.column.BooleanGridColumn;
import de.freese.base.core.model.grid.column.DateGridColumn;
import de.freese.base.core.model.grid.column.DoubleGridColumn;
import de.freese.base.core.model.grid.column.IntegerGridColumn;
import de.freese.base.core.model.grid.column.LongGridColumn;
import de.freese.base.core.model.grid.column.StringGridColumn;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(MockitoExtension.class)

// Sonst müsste pro Test-Methode der Mock als Parameter definiert und konfiguriert werden.
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestGrid
{
    /**
     * Erzeugt eine neue Instanz von {@link TestGrid}.
     */
    public TestGrid()
    {
        super();
    }

    /**
    *
    */
    @Test
    public void testGridBuilder()
    {
       //@formatter:off
       Grid grid = GridBuilder.create()
               .column(new IntegerGridColumn().name("int"))
               .column(Long.class)
                   .name("long")
               .and()
               .column(Double.class)
                   .name("double")
               .build();
       //@formatter:on

        assertEquals(3, grid.columnCount());
        assertEquals(0, grid.rowCount());

        assertEquals("int", grid.getColumnName(0));
        assertEquals("long", grid.getColumnName(1));
        assertEquals("double", grid.getColumnName(2));

        grid.addColumn(new BooleanGridColumn().name("boolean"));

        assertEquals(4, grid.columnCount());
        assertEquals("int", grid.getColumnName(0));
        assertEquals("long", grid.getColumnName(1));
        assertEquals("double", grid.getColumnName(2));
        assertEquals("boolean", grid.getColumnName(3));

        AbstractGridColumn<?> column = grid.removeColumn(1);
        assertNotNull(column);
        assertEquals("long", column.getName());

        assertEquals("int", grid.getColumnName(0));
        assertEquals("double", grid.getColumnName(1));
        assertEquals("boolean", grid.getColumnName(2));
    }

    /**
    *
    */
    @Test
    public void testGridBuilderFail()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //@formatter:off
            GridBuilder.create()
                .column(new IntegerGridColumn())
                .column(Integer.class)
                .build();
            //@formatter:on
        });

        assertNotNull(exception);
    }

    /**
     *
     */
    @Test
    public void testGridMetaDataAddColumnFail()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            GridMetaData gmd = new GridMetaData();

            gmd.addColumn(new IntegerGridColumn());
            gmd.addColumn(new LongGridColumn());

            assertEquals(2, gmd.columnCount());

            // Fehler erwartet
            gmd.addColumn(new IntegerGridColumn());
        });

        assertNotNull(exception);
    }

    /**
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    @Test
    public void testGridMetaSave() throws IOException, ClassNotFoundException
    {
        GridMetaData gmd = new GridMetaData();
        gmd.addColumn(new BinaryGridColumn());
        gmd.addColumn(new BooleanGridColumn().name("boolean").comment("test-boolean"));
        gmd.addColumn(new DateGridColumn().name("date").comment("test-date"));
        gmd.addColumn(new DoubleGridColumn().name("double").comment("test-double"));
        gmd.addColumn(new IntegerGridColumn());
        gmd.addColumn(new LongGridColumn().length(13).precision(42));
        gmd.addColumn(new StringGridColumn().name("string"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos))
        {
            gmd.write(dos);
        }

        byte[] bytes = baos.toByteArray();

        GridMetaData gmd2 = new GridMetaData();

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes)))
        {
            gmd2.read(dis);
        }

        assertEquals(7, gmd.columnCount());
        assertEquals(gmd.columnCount(), gmd2.columnCount());

        for (int i = 0; i < gmd.columnCount(); i++)
        {
            assertEquals(gmd.getColumnObjectClazz(i), gmd2.getColumnObjectClazz(i));
            assertEquals(gmd.getColumnName(i), gmd2.getColumnName(i));
            assertEquals(gmd.getColumnComment(i), gmd2.getColumnComment(i));
            assertEquals(gmd.getColumnLength(i), gmd2.getColumnLength(i));
            assertEquals(gmd.getColumnPrecision(i), gmd2.getColumnPrecision(i));
        }
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    public void testGridResultSet() throws SQLException
    {
        Object[][] data = new Object[][]
        {
                {
                        true, 1.23456D, "nur ein täschd"
                },
                {
                        false, 654321D, "-"
                }
        };

        ResultSetMetaData metaDataMock = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metaDataMock.getColumnCount()).then(invocation -> 3);

        Mockito.when(metaDataMock.getColumnType(ArgumentMatchers.eq(1))).then(invocation -> Types.BOOLEAN);
        Mockito.when(metaDataMock.getColumnLabel(ArgumentMatchers.eq(1))).then(invocation -> "boolean");
        Mockito.when(metaDataMock.getColumnDisplaySize(ArgumentMatchers.eq(1))).then(invocation -> 1);
        Mockito.when(metaDataMock.getPrecision(ArgumentMatchers.eq(1))).then(invocation -> 1);

        Mockito.when(metaDataMock.getColumnType(2)).thenReturn(Types.DOUBLE);
        Mockito.when(metaDataMock.getColumnLabel(2)).thenReturn("double");
        Mockito.when(metaDataMock.getColumnDisplaySize(2)).thenReturn(3);
        Mockito.when(metaDataMock.getPrecision(2)).thenReturn(3);

        Mockito.when(metaDataMock.getColumnType(3)).thenReturn(Types.VARCHAR);
        Mockito.when(metaDataMock.getColumnLabel(3)).thenReturn("string");
        Mockito.when(metaDataMock.getColumnDisplaySize(3)).thenReturn(10);
        Mockito.when(metaDataMock.getPrecision(3)).thenReturn(10);

        @SuppressWarnings("resource")
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(resultSetMock.getMetaData()).thenReturn(metaDataMock);

        AtomicInteger resultSetIndex = new AtomicInteger(-1);
        Mockito.when(resultSetMock.next()).then(invocation -> {
            resultSetIndex.getAndIncrement();
            return resultSetIndex.get() < data.length;
        });

        when(resultSetMock.getObject(1)).then(invocation -> {
            return data[resultSetIndex.get()][0];
        });
        when(resultSetMock.getObject(2)).then(invocation -> {
            return data[resultSetIndex.get()][1];
        });
        when(resultSetMock.getObject(3)).then(invocation -> {
            return data[resultSetIndex.get()][2];
        });

        Grid grid = new Grid();
        grid.read(resultSetMock);

        assertEquals(3, grid.columnCount());
        assertEquals(data.length, grid.rowCount());

        assertEquals(Boolean.class, grid.getColumnObjectClazz(0));
        assertEquals("boolean", grid.getColumnName(0));
        assertEquals(null, grid.getColumnComment(0));
        assertEquals(1, grid.getColumnLength(0));
        assertEquals(1, grid.getColumnPrecision(0));

        assertEquals(Double.class, grid.getColumnObjectClazz(1));
        assertEquals("double", grid.getColumnName(1));
        assertEquals(null, grid.getColumnComment(1));
        assertEquals(3, grid.getColumnLength(1));
        assertEquals(3, grid.getColumnPrecision(1));

        assertEquals(String.class, grid.getColumnObjectClazz(2));
        assertEquals("string", grid.getColumnName(2));
        assertEquals(null, grid.getColumnComment(2));
        assertEquals(10, grid.getColumnLength(2));
        assertEquals(10, grid.getColumnPrecision(2));

        for (int r = 0; r < grid.rowCount(); r++)
        {
            for (int c = 1; c < grid.columnCount(); c++)
            {
                assertEquals(data[r][c], grid.getValue(c, r));
            }
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     * @throws ClassNotFoundException Falls was schief geht.
     */
    @Test
    public void testGridSave() throws IOException, ClassNotFoundException
    {
        Grid grid1 = new Grid();
        grid1.addColumn(new BinaryGridColumn());
        grid1.addColumn(new BooleanGridColumn());
        grid1.addColumn(new DateGridColumn());
        grid1.addColumn(new DoubleGridColumn());
        grid1.addColumn(new IntegerGridColumn());
        grid1.addColumn(new LongGridColumn());
        grid1.addColumn(new StringGridColumn());

        Object[] row1 = new Object[]
        {
                new byte[]
                {
                        1, 2, 3
                }, false, new Date(), 1.23456D, 42, 123456L, "dies ist ein täschd"
        };
        grid1.addRow(row1);

        Object[] row2 = new Object[]
        {
                new byte[]
                {
                        4, 5, 6
                }, true, new Date(System.currentTimeMillis() - 200000), 6.54321D, 24, 654321L, ",.-#äöü+"
        };
        grid1.addRow(row2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos))
        {
            grid1.write(dos);
        }

        byte[] bytes = baos.toByteArray();

        Grid grid2 = new Grid(new GridMetaData());

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes)))
        {
            grid2.read(dis);
        }

        assertEquals(7, grid1.columnCount());
        assertEquals(2, grid1.rowCount());
        assertEquals(grid1.columnCount(), grid2.columnCount());
        assertEquals(grid1.rowCount(), grid2.rowCount());

        for (int i = 0; i < grid1.columnCount(); i++)
        {
            assertEquals(grid1.getColumnObjectClazz(i), grid2.getColumnObjectClazz(i));
            assertEquals(grid1.getColumnName(i), grid2.getColumnName(i));
            assertEquals(grid1.getColumnComment(i), grid2.getColumnComment(i));
            assertEquals(grid1.getColumnLength(i), grid2.getColumnLength(i));
            assertEquals(grid1.getColumnPrecision(i), grid2.getColumnPrecision(i));
        }

        for (int r = 0; r < grid1.rowCount(); r++)
        {
            byte[] a1 = grid1.getValue(0, r);
            byte[] a2 = grid2.getValue(0, r);

            assertArrayEquals(a1, a2);

            for (int c = 1; c < grid1.columnCount(); c++)
            {
                Object value1 = grid1.getValue(c, r);
                Object value2 = grid2.getValue(c, r);

                assertEquals(value1, value2);
            }
        }
    }
}
