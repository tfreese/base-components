/**
 * Created: 10.05.2018
 */
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
 * Liefert einen Datensatz als Map<String, Object>.
 *
 * @author Thomas Freese
 */
public class ColumnMapRowMapper implements RowMapper<Map<String, Object>>
{
    /**
     *
     */
    private String[] columnNames;

    /**
     * Ermittelt den Namen der Spalte am Index.
     *
     * @param resultSetMetaData {@link ResultSetMetaData}
     * @param index int; JDBC-Indices beginnen mit 1
     * @return String
     * @throws SQLException Falls was schief geht.
     */
    protected String getColumnName(final ResultSetMetaData resultSetMetaData, final int index) throws SQLException
    {
        String name = resultSetMetaData.getColumnLabel(index);

        if ((name == null) || (name.length() < 1))
        {
            name = resultSetMetaData.getColumnName(index);
        }

        return name.toUpperCase();
    }

    /**
     * @return String[]
     */
    protected String[] getColumnNames()
    {
        return this.columnNames;
    }

    /**
     * Liefert die Spaltennamen des {@link ResultSet}s.
     *
     * @param resultSet {@link ResultSet}
     * @return String[]
     * @throws SQLException Falls was schief geht.
     */
    protected String[] getColumnNames(final ResultSet resultSet) throws SQLException
    {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // Keys aufbauen
        String[] names = new String[columnCount];

        for (int i = 0; i < columnCount; i++)
        {
            String key = getColumnName(rsmd, i + 1);

            names[i] = key;
        }

        return names;
    }

    /**
     * Liefert das Value der Spalte am Index.
     *
     * @param rs {@link ResultSet}
     * @param index int; JDBC-Indices beginnen mit 1
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    protected Object getColumnValue(final ResultSet rs, final int index) throws SQLException
    {
        Object obj = rs.getObject(index);
        String className = null;

        if (obj != null)
        {
            className = obj.getClass().getName();
        }

        if (obj instanceof Blob)
        {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        }
        else if (obj instanceof Clob)
        {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        }
        else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))
        {
            obj = rs.getTimestamp(index);
        }
        else if ((className != null) && className.startsWith("oracle.sql.DATE"))
        {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);

            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName))
            {
                obj = rs.getTimestamp(index);
            }
            else
            {
                obj = rs.getDate(index);
            }
        }
        else if (obj instanceof java.sql.Date)
        {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index)))
            {
                obj = rs.getTimestamp(index);
            }
        }

        return obj;
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.RowMapper#mapRow(java.sql.ResultSet)
     */
    @Override
    public Map<String, Object> mapRow(final ResultSet resultSet) throws SQLException
    {
        if (getColumnNames() == null)
        {
            setColumnNames(getColumnNames(resultSet));
        }

        Map<String, Object> map = new LinkedHashMap<>(getColumnNames().length);

        for (int i = 0; i < getColumnNames().length; i++)
        {
            String columnName = this.columnNames[i];
            Object obj = getColumnValue(resultSet, i + 1);

            map.put(columnName, obj);
        }

        return map;
    }

    /**
     * @param columnNames String[]
     */
    protected void setColumnNames(final String[] columnNames)
    {
        this.columnNames = columnNames;
    }
}
