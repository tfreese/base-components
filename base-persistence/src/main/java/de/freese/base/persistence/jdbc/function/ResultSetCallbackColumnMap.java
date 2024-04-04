// Created: 12.11.23
package de.freese.base.persistence.jdbc.function;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class ResultSetCallbackColumnMap implements ResultSetCallback<List<Map<String, Object>>> {
    private String[] columnNames;

    @Override
    public List<Map<String, Object>> doInResultSet(final ResultSet resultSet) throws SQLException {
        if (this.columnNames == null) {
            this.columnNames = getColumnNames(resultSet);
        }

        final List<Map<String, Object>> list = new ArrayList<>();

        while (resultSet.next()) {
            final Map<String, Object> map = LinkedHashMap.newLinkedHashMap(this.columnNames.length);

            for (int i = 1; i <= this.columnNames.length; i++) {
                final String columnName = this.columnNames[i - 1];
                final Object obj = getColumnValue(resultSet, i);

                map.put(columnName, obj);
            }

            list.add(map);
        }

        return list;
    }

    protected String getColumnName(final ResultSetMetaData resultSetMetaData, final int index) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(index);

        if (name == null || name.isEmpty()) {
            name = resultSetMetaData.getColumnName(index);
        }

        return name.toUpperCase();
    }

    protected String[] getColumnNames(final ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        final String[] names = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            final String key = getColumnName(metaData, i + 1);

            names[i] = key;
        }

        return names;
    }

    protected Object getColumnValue(final ResultSet rs, final int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;

        if (obj != null) {
            className = obj.getClass().getName();
        }

        if (obj instanceof Blob blob) {
            obj = blob.getBytes(1, (int) blob.length());
        }
        else if (obj instanceof Clob clob) {
            obj = clob.getSubString(1, (int) clob.length());
        }
        else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getTimestamp(index);
        }
        else if (className != null && className.startsWith("oracle.sql.DATE")) {
            final String metaDataClassName = rs.getMetaData().getColumnClassName(index);

            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            }
            else {
                obj = rs.getDate(index);
            }
        }
        else if (obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }

        return obj;
    }
}
