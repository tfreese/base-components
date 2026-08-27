package de.freese.base.core.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.ObjIntConsumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Einfaches, serialisierungsfreundliches Datenmodell für JSON und XML.
 * <p/>
 * Prompt:
 * Es soll für Java 25 ein generisches Datenmodell entwickelt werden.
 * Das Datenmodell soll für die JSON und XML Serialisierung optimiert sein.
 * Die Erzeugung aller internen Objekte soll über einen Builder erfolgen.
 * Der Anwender muss auf Spalten, Zeilen und Zellen zugreifen können.
 * Zellen und Spalten müssen MetaDaten enthalten können.
 * Die MetaDaten sollen in einem eigenen Objekt gekapselt werden.
 * Der Zugriff auf die Daten und MetaDaten soll über die Spalten und Zeilen erfolgen.
 * Verwende nur Generics, wenn unbedingt notwendig.
 * Halte den Code einfach und verwende nur innere Klassen.
 * Benutze keine Snapshot Objekte.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"columns", "rows"})
@JsonDeserialize(builder = AiGeneratedDataModel.Builder.class)
@SuppressWarnings({"java:S2065"})
public final class AiGeneratedDataModel {

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private final List<Column> columns = new ArrayList<>();
        private final List<Row> rows = new ArrayList<>();

        public AiGeneratedDataModel build() {
            final AiGeneratedDataModel model = new AiGeneratedDataModel();
            model.columns.addAll(columns);
            model.rows.addAll(rows);
            model.attachModelReferences();

            return model;
        }

        public Builder columns(final List<Column> sourceColumns) {
            columns.clear();

            if (sourceColumns != null) {
                columns.addAll(sourceColumns);
            }

            return this;
        }

        public Builder rows(final List<Row> sourceRows) {
            rows.clear();

            if (sourceRows != null) {
                rows.addAll(sourceRows);
            }

            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({"value", "metadata"})
    @JsonDeserialize(builder = Cell.Builder.class)
    public static final class Cell {
        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {
            private Metadata metadata;
            private Object value;

            public Cell build() {
                final Cell cell = new Cell();
                cell.metadata = metadata == null ? Metadata.builder().build() : metadata;
                cell.value = value;

                return cell;
            }

            public Builder metadata(final Metadata metadata) {
                this.metadata = metadata;
                return this;
            }

            public Builder value(final Object value) {
                this.value = value;
                return this;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonProperty("metadata")
        private Metadata metadata;

        @JsonProperty("value")
        private Object value;

        private Cell() {
            super();
        }

        public Object getValue() {
            return value;
        }

        public Metadata metadata() {
            return metadata;
        }

        public void setValue(final Object value) {
            this.value = value;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({"index", "name", "typeName", "metadata"})
    @JsonDeserialize(builder = Column.Builder.class)
    public static final class Column {
        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {
            private int index;
            private Metadata metadata;
            private String name;
            private String typeName;

            public Column build() {
                final Column column = new Column();
                column.index = index;
                column.metadata = metadata == null ? Metadata.builder().build() : metadata;
                column.name = name;
                column.typeName = typeName == null || typeName.isBlank() ? Object.class.getName() : typeName;

                return column;
            }

            public Builder index(final int index) {
                this.index = index;
                return this;
            }

            public Builder metadata(final Metadata metadata) {
                this.metadata = metadata;
                return this;
            }

            public Builder name(final String name) {
                this.name = name;
                return this;
            }

            public Builder typeName(final String typeName) {
                this.typeName = typeName;
                return this;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonProperty("index")
        private int index;

        @JsonProperty("metadata")
        private Metadata metadata;

        @JsonIgnore
        private transient AiGeneratedDataModel model;

        @JsonProperty("name")
        private String name;

        @JsonProperty("typeName")
        private String typeName;

        private Column() {
            super();
        }

        public Metadata cellMetadata(final int rowIndex) {
            return requireModel().cell(rowIndex, index).metadata();
        }

        public Object getValue(final int rowIndex) {
            return requireModel().cell(rowIndex, index).getValue();
        }

        public int index() {
            return index;
        }

        public Metadata metadata() {
            return metadata;
        }

        public String name() {
            return name;
        }

        public void setValue(final int rowIndex, final Object value) {
            requireModel().cell(rowIndex, index).setValue(value);
        }

        @Override
        public String toString() {
            return "Column[index=%d, name=%s, typeName=%s]".formatted(index, name, typeName);
        }

        public String typeName() {
            return typeName;
        }

        private AiGeneratedDataModel requireModel() {
            if (model == null) {
                throw new IllegalStateException("Column is not attached to a model");
            }

            return model;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(builder = Metadata.Builder.class)
    public static final class Metadata {
        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {
            private final Map<String, Object> values = new LinkedHashMap<>();

            public Metadata build() {
                final Metadata metadata = new Metadata();
                metadata.values.putAll(values);

                return metadata;
            }

            public Builder put(final String key, final Object value) {
                values.put(requiredKey(key), value);
                return this;
            }

            public Builder values(final Map<String, Object> sourceValues) {
                if (sourceValues != null) {
                    values.putAll(sourceValues);
                }

                return this;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        private static String requiredKey(final String key) {
            return Objects.requireNonNull(key, "key required");
        }

        @JsonProperty("values")
        private Map<String, Object> values = new LinkedHashMap<>();

        private Metadata() {
            super();
        }

        public void clear() {
            values.clear();
        }

        public boolean contains(final String key) {
            return values.containsKey(requiredKey(key));
        }

        public Object get(final String key) {
            return values.get(requiredKey(key));
        }

        public Object getRequired(final String key) {
            final String actualKey = requiredKey(key);

            if (!values.containsKey(actualKey)) {
                throw new NoSuchElementException("Metadata key not found: " + actualKey);
            }

            return values.get(actualKey);
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }

        public Set<String> keys() {
            return Set.copyOf(values.keySet());
        }

        public Object put(final String key, final Object value) {
            return values.put(requiredKey(key), value);
        }

        public Object remove(final String key) {
            return values.remove(requiredKey(key));
        }

        public int size() {
            return values.size();
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder({"index", "cells"})
    @JsonDeserialize(builder = Row.Builder.class)
    public static final class Row {
        @JsonPOJOBuilder(withPrefix = "")
        public static final class Builder {
            private final List<Cell> cells = new ArrayList<>();
            private int index;

            public Builder addCell(final Cell cell) {
                cells.add(cell == null ? Cell.builder().build() : cell);
                return this;
            }

            public Row build() {
                final Row row = new Row();
                row.index = index;
                row.cells = new ArrayList<>(cells);

                return row;
            }

            public Builder cells(final List<Cell> sourceCells) {
                cells.clear();

                if (sourceCells != null) {
                    for (final Cell cell : sourceCells) {
                        cells.add(cell == null ? Cell.builder().build() : cell);
                    }
                }

                return this;
            }

            public Builder index(final int index) {
                this.index = index;
                return this;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @JsonProperty("cells")
        @JacksonXmlElementWrapper(localName = "cells")
        @JacksonXmlProperty(localName = "cell")
        private List<Cell> cells = new ArrayList<>();

        @JsonProperty("index")
        private int index;

        @JsonIgnore
        private transient AiGeneratedDataModel model;

        private Row() {
            super();
        }

        public Metadata cellMetadata(final String columnName) {
            return cell(requireModel().column(columnName).index()).metadata();
        }

        public Metadata cellMetadata(final int columnIndex) {
            return cell(columnIndex).metadata();
        }

        public Object getValue(final int columnIndex) {
            return cell(columnIndex).getValue();
        }

        public Object getValue(final String columnName) {
            return cell(requireModel().column(columnName).index()).getValue();
        }

        public int index() {
            return index;
        }

        public void setValue(final int columnIndex, final Object value) {
            cell(columnIndex).setValue(value);
        }

        public void setValue(final String columnName, final Object value) {
            cell(requireModel().column(columnName).index()).setValue(value);
        }

        @Override
        public String toString() {
            return "Row[index=%d]".formatted(index);
        }

        Cell cell(final String columnName) {
            return cell(requireModel().column(columnName).index());
        }

        Cell cell(final int columnIndex) {
            if (columnIndex < 0 || columnIndex >= cells.size()) {
                throw new IndexOutOfBoundsException("Column index %d outside range [0, %d) for row %d".formatted(columnIndex, cells.size(), index));
            }

            return cells.get(columnIndex);
        }

        private AiGeneratedDataModel requireModel() {
            if (model == null) {
                throw new IllegalStateException("Row is not attached to a model");
            }

            return model;
        }
    }

    static void main() {
        final AiGeneratedDataModel model = new AiGeneratedDataModel();
        final AiGeneratedDataModel.Column columnId = model.addColumn("id", Long.class);
        model.addColumn("name", String.class);

        model.column(0).metadata().put("hint", "serializable");

        final AiGeneratedDataModel.Row row0 = model.addRow();
        row0.setValue(columnId.name(), 1001L);
        row0.setValue(1, "Alice");
        row0.cellMetadata(1).put("quality", "verified");

        final AiGeneratedDataModel.Row row1 = model.addRow();
        row1.setValue(columnId.name(), 2002L);
        row1.setValue(1, "Bob");

        model.print(System.out);
    }

    @JsonIgnore
    private final Map<String, Integer> columnIndexByName = new LinkedHashMap<>();

    @JsonProperty("columns")
    @JacksonXmlElementWrapper(localName = "columns")
    @JacksonXmlProperty(localName = "column")
    private final List<Column> columns = new ArrayList<>();

    @JsonProperty("rows")
    @JacksonXmlElementWrapper(localName = "rows")
    @JacksonXmlProperty(localName = "row")
    private final List<Row> rows = new ArrayList<>();

    public Column addColumn(final String name, final Class<?> type) {
        return addColumn(name, type == null ? null : type.getName());
    }

    public Column addColumn(final String name, final String typeName) {
        Objects.requireNonNull(name, "name required");

        if (columnIndexByName.containsKey(name)) {
            throw new IllegalStateException("Column with name " + name + " already exists");
        }

        final Column column = Column.builder()
                .index(columns.size())
                .name(name)
                .typeName(typeName)
                .build();
        column.model = this;
        columns.add(column);
        columnIndexByName.put(name, column.index());

        for (final Row row : rows) {
            row.cells.add(Cell.builder().build());
        }

        return column;
    }

    public Row addRow() {
        final Row.Builder rowBuilder = Row.builder().index(rows.size());

        for (int i = 0; i < columns.size(); i++) {
            rowBuilder.addCell(Cell.builder().build());
        }

        final Row row = rowBuilder.build();
        row.model = this;
        rows.add(row);

        return row;
    }

    public Column column(final int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Column index %d outside range [0, %d)".formatted(columnIndex, columns.size()));
        }

        return columns.get(columnIndex);
    }

    public Column column(final String columnName) {
        final Integer columnIndex = columnIndexByName.get(columnName);

        if (columnIndex == null || columnIndex < 0 || columnIndex >= columns.size()) {
            throw new NoSuchElementException("Column name not found: " + columnName);
        }

        return columns.get(columnIndex);
    }

    public int columnCount() {
        return columns.size();
    }

    public List<String> columnNames() {
        return columns.stream().map(Column::name).toList();
    }

    public void print(final PrintStream printStream) {
        Objects.requireNonNull(printStream, "printStream required");

        if (columns.isEmpty()) {
            printStream.println(getClass().getSimpleName() + "[no columns]");
            return;
        }

        final int columnCount = columns.size();
        final int[] widths = new int[columnCount];

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            widths[columnIndex] = columns.get(columnIndex).name().length();

            for (final Row row : rows) {
                final String value = String.valueOf(row.getValue(columnIndex));
                widths[columnIndex] = Math.max(widths[columnIndex], value.length());
            }
        }

        final ObjIntConsumer<String> writePaddedCell = (value, width) -> {
            printStream.print(' ');
            printStream.print(value);

            for (int i = 0; i < width - value.length(); i++) {
                printStream.print(' ');
            }

            printStream.print(" |");
        };

        final Runnable writeBorder = () -> {
            printStream.print('+');

            for (final int width : widths) {
                for (int i = 0; i < width + 2; i++) {
                    printStream.print('-');
                }

                printStream.print('+');
            }

            printStream.println();
        };

        final Runnable writeHeader = () -> {
            printStream.print('|');

            for (int columnIndex = 0; columnIndex < widths.length; columnIndex++) {
                writePaddedCell.accept(columns.get(columnIndex).name(), widths[columnIndex]);
            }

            printStream.println();
        };

        writeBorder.run();
        writeHeader.run();
        writeBorder.run();

        for (final Row row : rows) {
            printStream.print('|');

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                final String value = String.valueOf(row.getValue(columnIndex));
                writePaddedCell.accept(value, widths[columnIndex]);
            }

            printStream.println();
        }

        writeBorder.run();
    }

    public Row row(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            throw new IndexOutOfBoundsException("Row index %d outside range [0, %d)".formatted(rowIndex, rows.size()));
        }

        return rows.get(rowIndex);
    }

    public int rowCount() {
        return rows.size();
    }

    @Override
    public String toString() {
        return "%s[columns=%d, rows=%d]".formatted(getClass().getSimpleName(), columns.size(), rows.size());
    }

    private void attachModelReferences() {
        columnIndexByName.clear();

        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            final Column column = columns.get(columnIndex);

            if (column == null) {
                throw new IllegalStateException("Column entry must not be null at index " + columnIndex);
            }

            column.model = this;
            column.index = columnIndex;

            if (column.metadata == null) {
                column.metadata = Metadata.builder().build();
            }

            if (column.name == null || column.name.isBlank()) {
                throw new IllegalStateException("Column name must not be empty at index " + columnIndex);
            }

            if (columnIndexByName.putIfAbsent(column.name, columnIndex) != null) {
                throw new IllegalStateException("Duplicate column name: " + column.name);
            }
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            final Row row = rows.get(rowIndex);

            if (row == null) {
                throw new IllegalStateException("Row entry must not be null at index " + rowIndex);
            }

            row.model = this;

            if (row.cells == null) {
                row.cells = new ArrayList<>();
            }

            while (row.cells.size() < columns.size()) {
                row.cells.add(Cell.builder().build());
            }

            while (row.cells.size() > columns.size()) {
                row.cells.removeLast();
            }

            for (int columnIndex = 0; columnIndex < row.cells.size(); columnIndex++) {
                Cell cell = row.cells.get(columnIndex);

                if (cell == null) {
                    cell = Cell.builder().build();
                    row.cells.set(columnIndex, cell);
                }

                if (cell.metadata == null) {
                    cell.metadata = Metadata.builder().build();
                }
            }
        }
    }

    private Cell cell(final int rowIndex, final int columnIndex) {
        return row(rowIndex).cell(columnIndex);
    }
}
