// Created: 29 Okt. 2025
package de.freese.base.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Thomas Freese
 */
@JsonPropertyOrder(alphabetic = true)
public final class GenericModel {
    @JsonPropertyOrder({"name", "type", "metaData"})
    public static class GenericColumn {
        public static GenericColumn of(final String name) {
            final GenericColumn column = new GenericColumn();
            column.setName(name);

            return column;
        }

        /**
         * Map.of() throws NullPointerException if values are null.
         */
        public static GenericColumn of(final String name, final Map<String, String> metaData) {
            final GenericColumn genericColumn = of(name);

            metaData.forEach(genericColumn::putMetaData);

            return genericColumn;
        }

        private Map<String, String> metaData = new HashMap<>();
        private String name;
        private Class<?> type;

        public Map<String, String> getMetaData() {
            return metaData;
        }

        public String getMetaData(final String key) {
            return metaData.get(key);
        }

        public String getMetaData(final String key, final String defaultValue) {
            return metaData.getOrDefault(key, defaultValue);
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public void putMetaData(final String key, final String value) {
            metaData.put(key, value);
        }

        public void setMetaData(final Map<String, String> metaData) {
            this.metaData = Objects.requireNonNull(metaData, "metaData required");
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setType(final Class<?> type) {
            this.type = type;
        }
    }

    public static class GenericRow {
        private Map<String, Object> values = new HashMap<>();

        public Object getValue(final String columnName) {
            return values.get(columnName);
        }

        public Object getValue(final String columnName, final Object defaultValue) {
            return values.getOrDefault(columnName, defaultValue);
        }

        public Map<String, Object> getValues() {
            return values;
        }

        @JsonIgnore
        public boolean isEmpty() {
            return values.isEmpty();
        }

        public void putValue(final String columnName, final Object value) {
            values.put(columnName, value);
        }

        public void removeValue(final String columnName) {
            values.remove(columnName);
        }

        public void setValues(final Map<String, Object> values) {
            this.values = Objects.requireNonNull(values, "values required");
        }
    }

    @JsonIgnore
    private final Map<String, GenericColumn> columnMap = new HashMap<>();

    private List<GenericColumn> columns = new ArrayList<>();
    private List<GenericRow> rows = new ArrayList<>();

    public void addColumn(final GenericColumn column) {
        Objects.requireNonNull(column, "column required");

        columns.add(column);

        columnMap.put(column.getName(), column);
    }

    public void addRow(final GenericRow row) {
        Objects.requireNonNull(row, "row required");

        rows.add(row);
    }

    public GenericColumn getColumn(final int index) {
        return columns.get(index);
    }

    public GenericColumn getColumn(final String name) {
        return columnMap.computeIfAbsent(name, key -> columns.stream().filter(column -> column.getName().equals(name)).findFirst().orElse(null));
    }

    @JsonIgnore
    public List<String> getColumnNames() {
        return columns.stream().map(GenericColumn::getName).toList();
    }

    public List<GenericColumn> getColumns() {
        return columns;
    }

    public GenericRow getRow(final int index) {
        return rows.get(index);
    }

    public Object getRowValue(final int rowIndex, final String columnName) {
        return getRow(rowIndex).getValue(columnName);
    }

    public List<GenericRow> getRows() {
        return rows;
    }

    public void removeRow(final GenericRow row) {
        rows.remove(row);
    }

    public void setColumns(final List<GenericColumn> columns) {
        this.columns = Objects.requireNonNull(columns, "columns required");

        columnMap.clear();

        columns.forEach(c -> columnMap.put(c.getName(), c));
    }

    public void setRows(final List<GenericRow> rows) {
        this.rows = Objects.requireNonNull(rows, "rows required");
    }
}
