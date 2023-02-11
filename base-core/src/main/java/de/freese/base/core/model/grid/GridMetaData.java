// Created: 24.01.2018
package de.freese.base.core.model.grid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.freese.base.core.model.grid.column.GridColumn;
import de.freese.base.core.model.grid.factory.DefaultGridColumnFactory;
import de.freese.base.core.model.grid.factory.GridColumnFactory;

/**
 * @author Thomas Freese
 */
public class GridMetaData {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final List<GridColumn<?>> columns = new ArrayList<>();

    private final GridColumnFactory gridColumnFactory;

    public GridMetaData() {
        this(new DefaultGridColumnFactory());
    }

    public GridMetaData(final GridColumnFactory gridColumnFactory) {
        super();

        this.gridColumnFactory = Objects.requireNonNull(gridColumnFactory, "gridColumnFactory required");
    }

    public void addColumn(final GridColumn<?> column) {
        getColumns().add(column);
    }

    public GridColumn<?> getColumn(final int columnIndex) {
        return getColumns().get(columnIndex);
    }

    public int getColumnCount() {
        return getColumns().size();
    }

    public GridColumnFactory getGridColumnFactory() {
        return this.gridColumnFactory;
    }

    public void readMetaData(final DataInput dataInput) throws IOException, ClassNotFoundException {
        int columnCount = dataInput.readInt();

        for (int i = 0; i < columnCount; i++) {
            int length = dataInput.readInt();
            byte[] bytes = new byte[length];
            dataInput.readFully(bytes);

            String typeName = new String(bytes, DEFAULT_CHARSET);
            Class<?> type = Class.forName(typeName);
            GridColumn<?> column = this.gridColumnFactory.getColumnForType(type);

            getColumns().add(column);

            boolean isNull = dataInput.readBoolean(); // NULL-Marker

            if (!isNull) {
                length = dataInput.readInt();
                bytes = new byte[length];
                dataInput.readFully(bytes);

                String name = new String(bytes, GridMetaData.DEFAULT_CHARSET);
                column.setName(name);
            }

            length = dataInput.readInt();
            column.setLength(length);

            int precision = dataInput.readInt();
            column.setPrecision(precision);

            isNull = dataInput.readBoolean(); // NULL-Marker

            if (!isNull) {
                length = dataInput.readInt();
                bytes = new byte[length];
                dataInput.readFully(bytes);

                String comment = new String(bytes, GridMetaData.DEFAULT_CHARSET);
                column.setComment(comment);
            }
        }
    }

    public void readMetaData(final ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();

        for (int c = 1; c <= columnCount; c++) {
            GridColumn<?> column = this.gridColumnFactory.getColumnForSQL(metaData.getColumnType(c));
            column.setName(metaData.getColumnLabel(c));
            column.setLength(metaData.getColumnDisplaySize(c));
            column.setPrecision(metaData.getPrecision(c));

            getColumns().add(column);
        }
    }

    public GridColumn<?> removeColumn(final int columnIndex) {
        return getColumns().remove(columnIndex);
    }

    public void writeMetaData(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(getColumnCount());

        for (GridColumn<?> column : getColumns()) {
            byte[] bytes = column.getType().getName().getBytes(DEFAULT_CHARSET);
            dataOutput.writeInt(bytes.length);
            dataOutput.write(bytes);

            String name = Optional.ofNullable(column.getName()).orElse("").strip();
            name = name.length() == 0 ? null : name;

            dataOutput.writeBoolean(name == null); // NULL-Marker

            if (name != null) {
                bytes = name.getBytes(DEFAULT_CHARSET);
                dataOutput.writeInt(bytes.length);
                dataOutput.write(bytes);
            }

            dataOutput.writeInt(column.getLength());

            dataOutput.writeInt(column.getPrecision());

            String comment = Optional.ofNullable(column.getComment()).orElse("").strip();
            comment = comment.length() == 0 ? null : comment;

            dataOutput.writeBoolean(comment == null); // NULL-Marker

            if (comment != null) {
                bytes = comment.getBytes(DEFAULT_CHARSET);
                dataOutput.writeInt(bytes.length);
                dataOutput.write(bytes);
            }
        }
    }

    protected List<GridColumn<?>> getColumns() {
        return this.columns;
    }
}
