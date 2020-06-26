// Created: 02.07.2009
package de.freese.base.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilklasse fuer das Arbeiten mit JDBC.
 *
 * @author Thomas Freese
 */
public final class JdbcUtils
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    /**
     * Schliesst die {@link Connection}.<br>
     *
     * @param connection {@link Connection}
     * @throws SQLException Falls was schief geht.
     */
    public static void closeConnection(final Connection connection) throws SQLException
    {
        // Spring-Variante
        // DataSourceUtils.releaseConnection(connection, getDataSource());

        if ((connection == null) || connection.isClosed())
        {
            return;
        }

        connection.close();
    }

    /**
     * Schliesst die {@link Connection}.<br>
     * Eine {@link SQLException} wird ignoriert.
     *
     * @param connection {@link Connection}
     */
    public static void closeConnectionSilent(final Connection connection)
    {
        try
        {
            closeConnection(connection);
        }
        catch (SQLException ex)
        {
            LOGGER.error("Could not close JDBC Connection", ex);
        }
        catch (Throwable ex)
        {
            // We don't trust the JDBC driver: It might throw RuntimeException or Error.
            LOGGER.error("Unexpected exception on closing JDBC Connection", ex);
        }
    }

    /**
     * Schliesst das {@link ResultSet}.<br>
     *
     * @param resultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    public static void closeResultSet(final ResultSet resultSet) throws SQLException
    {
        // Spring-Variante
        // DataSourceUtils.closeResultSet(resultSet);

        if ((resultSet == null) || resultSet.isClosed())
        {
            return;
        }

        resultSet.close();
    }

    /**
     * Schliesst das {@link ResultSet}.<br>
     * Eine {@link SQLException} wird ignoriert.
     *
     * @param resultSet {@link ResultSet}
     */
    public static void closeResultSetSilent(final ResultSet resultSet)
    {
        try
        {
            closeResultSet(resultSet);
        }
        catch (SQLException ex)
        {
            LOGGER.error("Could not close JDBC ResultSet", ex);
        }
        catch (Throwable ex)
        {
            // We don't trust the JDBC driver: It might throw RuntimeException or Error.
            LOGGER.error("Unexpected exception on closing JDBC ResultSet", ex);
        }
    }

    /**
     * Schliesst das {@link Statement}.<br>
     *
     * @param statement {@link Statement} geht.
     * @throws SQLException Falls was schief geht.
     */
    public static void closeStatement(final Statement statement) throws SQLException
    {
        // Spring-Variante
        // JdbcUtils.closeStatement(statement);

        if ((statement == null) || statement.isClosed())
        {
            return;
        }

        if (statement instanceof PreparedStatement)
        {
            statement.clearBatch();
        }

        statement.close();
    }

    /**
     * Schliesst das {@link Statement}.<br>
     * Eine {@link SQLException} wird ignoriert.
     *
     * @param statement {@link Statement} geht.
     */
    public static void closeStatementSilent(final Statement statement)
    {
        try
        {
            closeStatement(statement);
        }
        catch (SQLException ex)
        {
            LOGGER.error("Could not close JDBC Statement", ex);
        }
        catch (Throwable ex)
        {
            // We don't trust the JDBC driver: It might throw RuntimeException or Error.
            LOGGER.error("Unexpected exception on closing JDBC Statement", ex);
        }
    }

    /**
     * Erstellt einen String aus per komma getrennten ids.
     *
     * @param ids {@link Set}
     * @return {@link String}
     */
    public static String createIDsAsString(final Collection<? extends Number> ids)
    {
        boolean firstParameter = true;
        StringBuilder builder = new StringBuilder();

        for (Number each : ids)
        {
            if (!firstParameter)
            {
                builder.append(",");
            }
            else
            {
                firstParameter = false;
            }

            builder.append(each.toString());
        }

        return builder.toString();
    }

    /**
     * Erzeugt eine "in"-Clause und beruecksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein duerfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere "in"-Clause angehaengt.
     *
     * @param column String
     * @param sql {@link StringBuilder}
     * @param elements {@link Set}
     */
    public static void createInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements)
    {
        createInOrNotInClause(column, sql, elements, "in");
    }

    /**
     * Erzeugt eine "in"- oder "not in"-Clause und beruecksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein duerfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere Clause angehaengt.
     *
     * @param column String
     * @param sql {@link StringBuilder}
     * @param elements {@link Set}
     * @param inOrNotIn String
     */
    private static void createInOrNotInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements, final String inOrNotIn)
    {
        sql.append(column).append(" ").append(inOrNotIn).append(" (");

        if ((elements == null) || elements.isEmpty())
        {
            sql.append("-1)");

            return;
        }

        Iterator<? extends Number> iterator = elements.iterator();

        int i = 0;

        while (iterator.hasNext())
        {
            Number number = iterator.next();

            sql.append(number);
            i++;

            if (((i % 1000) == 0) && iterator.hasNext())
            {
                // Neuen Block anfangen,
                sql.append(") or ").append(column).append(" ").append(inOrNotIn).append(" (");
            }
            else if (iterator.hasNext())
            {
                sql.append(",");
            }
        }

        sql.append(")");
    }

    /**
     * Erzeugt eine "not in"-Clause und beruecksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein duerfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere "not in"-Clause angehaengt.
     *
     * @param column String
     * @param sql {@link StringBuilder}
     * @param elements {@link Set}
     */
    public static void createNotInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements)
    {
        createInOrNotInClause(column, sql, elements, "not in");
    }

    /**
     * @param dataSource {@link DataSource}
     * @param callback {@link Function}
     * @return Object
     * @throws SQLException Falls was schief geht.
     */
    public static <T> T extractDatabaseMetaData(final DataSource dataSource, final Function<DatabaseMetaData, T> callback) throws SQLException
    {
        try (Connection connection = Objects.requireNonNull(dataSource, "dataSource required").getConnection())
        {
            DatabaseMetaData metaData = Objects.requireNonNull(connection.getMetaData(), "metaData required");

            return callback.apply(metaData);
        }
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht false wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Boolean
     * @throws SQLException Falls was schief geht.
     */
    public static Boolean getBoolean(final CallableStatement cs, final int index) throws SQLException
    {
        boolean value = cs.getBoolean(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht false wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Boolean
     * @throws SQLException Falls was schief geht.
     */
    public static Boolean getBoolean(final ResultSet rs, final int index) throws SQLException
    {
        boolean value = rs.getBoolean(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht false wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Boolean
     * @throws SQLException Falls was schief geht.
     */
    public static Boolean getBoolean(final ResultSet rs, final String columnName) throws SQLException
    {
        return getBoolean(rs, rs.findColumn(columnName));
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Byte
     * @throws SQLException Falls was schief geht.
     */
    public static Byte getByte(final CallableStatement cs, final int index) throws SQLException
    {
        byte value = cs.getByte(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Byte
     * @throws SQLException Falls was schief geht.
     */
    public static Byte getByte(final ResultSet rs, final int index) throws SQLException
    {
        byte value = rs.getByte(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Byte
     * @throws SQLException Falls was schief geht.
     */
    public static Byte getByte(final ResultSet rs, final String columnName) throws SQLException
    {
        return getByte(rs, rs.findColumn(columnName));
    }

    /**
     * Liefert den Produktnamen der Datenbank.
     *
     * @param dataSource {@link DataSource}
     * @return String
     * @throws SQLException Falls was schief geht.
     * @see #extractDatabaseMetaData(DataSource, Function)
     */
    public static String getDatabaseProductName(final DataSource dataSource) throws SQLException
    {
        String databaseProductName = extractDatabaseMetaData(dataSource, dbMd -> {
            try
            {
                return dbMd.getDatabaseProductName();
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        });

        return databaseProductName;
    }

    /**
     * Liefert die ProduktVersion der Datenbank.
     *
     * @param dataSource {@link DataSource}
     * @return String
     * @throws SQLException Falls was schief geht.
     * @see #extractDatabaseMetaData(DataSource, Function)
     */
    public static String getDatabaseProductVersion(final DataSource dataSource) throws SQLException
    {
        String databaseProductVersion = extractDatabaseMetaData(dataSource, dbMd -> {
            try
            {
                return dbMd.getDatabaseProductVersion();
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        });

        return databaseProductVersion;
    }

    /**
     * @return {@link ClassLoader}
     */
    public static ClassLoader getDefaultClassLoader()
    {
        ClassLoader cl = null;

        try
        {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex)
        {
            // NO-OP
        }

        if (cl == null)
        {
            try
            {
                cl = JdbcUtils.class.getClassLoader();
            }
            catch (Throwable ex)
            {
                // NO-OP
            }
        }

        if (cl == null)
        {
            try
            {
                cl = ClassLoader.getSystemClassLoader();
            }
            catch (Throwable ex)
            {
                // NO-OP
            }
        }

        return cl;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Double
     * @throws SQLException Falls was schief geht.
     */
    public static Double getDouble(final CallableStatement cs, final int index) throws SQLException
    {
        double value = cs.getDouble(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Double
     * @throws SQLException Falls was schief geht.
     */
    public static Double getDouble(final ResultSet rs, final int index) throws SQLException
    {
        double value = rs.getDouble(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Double
     * @throws SQLException Falls was schief geht.
     */
    public static Double getDouble(final ResultSet rs, final String columnName) throws SQLException
    {
        return getDouble(rs, rs.findColumn(columnName));
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Float
     * @throws SQLException Falls was schief geht.
     */
    public static Float getFloat(final CallableStatement cs, final int index) throws SQLException
    {
        float value = cs.getFloat(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Float
     * @throws SQLException Falls was schief geht.
     */
    public static Float getFloat(final ResultSet rs, final int index) throws SQLException
    {
        float value = rs.getFloat(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Float
     * @throws SQLException Falls was schief geht.
     */
    public static Float getFloat(final ResultSet rs, final String columnName) throws SQLException
    {
        return getFloat(rs, rs.findColumn(columnName));
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Integer
     * @throws SQLException Falls was schief geht.
     */
    public static Integer getInteger(final CallableStatement cs, final int index) throws SQLException
    {
        int value = cs.getInt(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Integer
     * @throws SQLException Falls was schief geht.
     */
    public static Integer getInteger(final ResultSet rs, final int index) throws SQLException
    {
        int value = rs.getInt(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Integer
     * @throws SQLException Falls was schief geht.
     */
    public static Integer getInteger(final ResultSet rs, final String columnName) throws SQLException
    {
        return getInteger(rs, rs.findColumn(columnName));
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Long
     * @throws SQLException Falls was schief geht.
     */
    public static Long getLong(final CallableStatement cs, final int index) throws SQLException
    {
        long value = cs.getLong(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Long
     * @throws SQLException Falls was schief geht.
     */
    public static Long getLong(final ResultSet rs, final int index) throws SQLException
    {
        long value = rs.getLong(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Long
     * @throws SQLException Falls was schief geht.
     */
    public static Long getLong(final ResultSet rs, final String columnName) throws SQLException
    {
        return getLong(rs, rs.findColumn(columnName));
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param cs {@link CallableStatement}
     * @param index int
     * @return Short
     * @throws SQLException Falls was schief geht.
     */
    public static Short getShort(final CallableStatement cs, final int index) throws SQLException
    {
        short value = cs.getShort(index);

        return cs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param index int
     * @return Short
     * @throws SQLException Falls was schief geht.
     */
    public static Short getShort(final ResultSet rs, final int index) throws SQLException
    {
        short value = rs.getShort(index);

        return rs.wasNull() ? null : value;
    }

    /**
     * Wenn der Wert in SQL NULL ist wird null geliefert und nicht 0 wie in der Defaultimplementierung.
     *
     * @param rs {@link ResultSet}
     * @param columnName String
     * @return Short
     * @throws SQLException Falls was schief geht.
     */
    public static Short getShort(final ResultSet rs, final String columnName) throws SQLException
    {
        return getShort(rs, rs.findColumn(columnName));
    }

    /**
     * Fuegt zu der IN-Query die entsprechenden Werte hinzu.
     *
     * @param values {@link Set}
     * @param separator char
     * @return {@link StringBuilder}
     */
    public static StringBuilder parameterAsString(final Set<String> values, final char separator)
    {
        StringBuilder builder = new StringBuilder();

        for (Iterator<String> iter = values.iterator(); iter.hasNext();)
        {
            builder.append(iter.next());

            if (iter.hasNext())
            {
                builder.append(separator);
            }
        }

        return builder;
    }

    /**
     * Erzeugt aus dem {@link ResultSet} eine {@link StringTable}.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link StringTable}
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public static StringTable toStringTable(final ResultSet resultSet) throws SQLException
    {
        Objects.requireNonNull(resultSet, "resultSet required");

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Spaltennamen / Header
        String[] header = new String[columnCount];

        for (int column = 1; column <= columnCount; column++)
        {
            header[column - 1] = metaData.getColumnLabel(column).toUpperCase();
        }

        List<String[]> rows = new ArrayList<>();

        // Daten
        while (resultSet.next())
        {
            String[] row = new String[columnCount];
            rows.add(row);

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                String value = null;

                if (obj == null)
                {
                    value = "";
                }
                else if (obj instanceof byte[])
                {
                    value = new String((byte[]) obj);
                }
                else
                {
                    value = obj.toString();
                }

                row[column - 1] = value;
            }
        }

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }

        return new StringTable(header, rows);
    }

    /**
     * Erzeugt aus den {@link ResultSetMetaData} eine {@link StringTable}.<br>
     *
     * @param rsMeta {@link ResultSetMetaData}
     * @return {@link StringTable}
     * @throws SQLException Falls was schief geht.
     */
    public static StringTable toStringTable(final ResultSetMetaData rsMeta) throws SQLException
    {
        Objects.requireNonNull(rsMeta, "resultSetMetaData required");

        // Spaltennamen / Header
        String[] header = new String[5];
        header[0] = "Columnname";
        header[1] = "Classname";
        header[2] = "Typename";
        header[3] = "Type";
        header[4] = "Nullable";

        List<String[]> rows = new ArrayList<>();

        // Daten
        for (int col = 1; col <= rsMeta.getColumnCount(); col++)
        {
            String[] row = new String[5];
            rows.add(row);

            row[0] = rsMeta.getColumnName(col);
            row[1] = rsMeta.getColumnClassName(col);
            row[2] = rsMeta.getColumnTypeName(col);
            row[3] = "" + rsMeta.getColumnType(col);
            row[4] = "" + rsMeta.isNullable(col);
        }

        return new StringTable(header, rows);
    }

    /**
     * Schreibt das ResultSet in den PrintStream.<br>
     * Dabei wird die Spaltenbreite auf den breitesten Wert angepasst.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps {@link PrintStream}
     * @throws SQLException Falls was schief geht.
     */
    public static void write(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        StringTable stringTable = toStringTable(resultSet);
        stringTable.rightpad(" ");
        stringTable.write(ps, "-", " | ");

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }

    /**
     * Tabellarische Ausgabe der ResultSetMetaDaten.
     *
     * @param rsMeta {@link ResultSetMetaData}
     * @param ps {@link PrintStream}
     * @throws SQLException Falls was schief geht.
     */
    public static void write(final ResultSetMetaData rsMeta, final PrintStream ps) throws SQLException
    {
        StringTable stringTable = toStringTable(rsMeta);
        stringTable.rightpad(" ");
        stringTable.write(ps, "-", " | ");
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps {@link PrintWriter}
     * @throws SQLException Falls was schief geht.
     */
    public static void writeCSV(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        StringTable stringTable = toStringTable(resultSet);
        stringTable.escape('"');
        stringTable.write(ps, "", ";");

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }

    /**
     * Erstellt ein neues {@link JdbcUtils} Object.
     */
    private JdbcUtils()
    {
        super();
    }
}
