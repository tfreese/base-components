// Created: 10.05.2018
package de.freese.base.persistence.jdbc.template;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * Inspired by org.springframework.jdbc.core<br>
 *
 * @author Thomas Freese
 */
public class ColumnMapRowMapper implements RowMapper<Map<String, Object>> {
    
    private String[] columnNames;

    @Override
    public Map<String, Object> mapRow(final ResultSet resultSet) throws SQLException {
        if (this.columnNames == null) {
            this.columnNames = getColumnNames(resultSet);
        }

        Map<String, Object> map = new LinkedHashMap<>(this.columnNames.length);

        for (int i = 1; i <= this.columnNames.length; i++) {
            String columnName = this.columnNames[i - 1];
            Object obj = getColumnValue(resultSet, i);

            map.put(columnName, obj);
        }

        return map;
    }

    protected String getColumnName(final ResultSetMetaData resultSetMetaData, final int index) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(index);

        if ((name == null) || (name.length() < 1)) {
            name = resultSetMetaData.getColumnName(index);
        }

        return name.toUpperCase();
    }

    protected String[] getColumnNames(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[] names = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            String key = getColumnName(metaData, i + 1);

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
        else if ((className != null) && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);

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
