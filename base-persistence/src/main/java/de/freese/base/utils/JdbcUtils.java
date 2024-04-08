// Created: 02.07.2009
package de.freese.base.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class JdbcUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    public static void close(final Connection connection) throws SQLException {
        // Spring-Variante
        // DataSourceUtils.releaseConnection(connection, getDataSource());
        // JdbcUtils.closeConnection(connection)

        if (connection == null || connection.isClosed()) {
            return;
        }

        connection.close();
    }

    public static void close(final ResultSet resultSet) throws SQLException {
        // Spring-Variante
        // JdbcUtils.closeResultSet(resultSet);

        if (resultSet == null || resultSet.isClosed()) {
            return;
        }

        resultSet.close();
    }

    public static void close(final Statement statement) throws SQLException {
        // Spring-Variante
        // JdbcUtils.closeStatement(statement);

        if (statement == null || statement.isClosed()) {
            return;
        }

        if (statement instanceof PreparedStatement s) {
            s.clearBatch();
            s.clearParameters();
        }

        statement.close();
    }

    public static void closeSilent(final Connection connection) {
        try {
            close(connection);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC Connection", ex);
        }
    }

    public static void closeSilent(final ResultSet resultSet) {
        try {
            close(resultSet);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC ResultSet", ex);
        }
    }

    public static void closeSilent(final Statement statement) {
        try {
            close(statement);
        }
        catch (Exception ex) {
            LOGGER.error("Could not close JDBC Statement", ex);
        }
    }

    public static String createIDsAsString(final Iterable<? extends Number> ids) {
        return StreamSupport.stream(ids.spliterator(), false).map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Erzeugt eine "in"-Clause und berücksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein dürfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere "in"-Clause angehängt.
     */
    public static void createInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements) {
        createInOrNotInClause(column, sql, elements, "in");
    }

    /**
     * Erzeugt eine "not in"-Clause und berücksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein dürfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere "not in"-Clause angehängt.
     */
    public static void createNotInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements) {
        createInOrNotInClause(column, sql, elements, "not in");
    }

    public static <T> T extractDatabaseMetaData(final DataSource dataSource, final Function<DatabaseMetaData, T> callback) throws SQLException {
        try (Connection connection = Objects.requireNonNull(dataSource, "dataSource required").getConnection()) {
            final DatabaseMetaData metaData = Objects.requireNonNull(connection.getMetaData(), "metaData required");

            return callback.apply(metaData);
        }
    }

    public static Boolean getBoolean(final CallableStatement cs, final int index) throws SQLException {
        final boolean value = cs.getBoolean(index);

        return cs.wasNull() ? null : value;
    }

    public static Boolean getBoolean(final ResultSet rs, final int index) throws SQLException {
        final boolean value = rs.getBoolean(index);

        return rs.wasNull() ? null : value;
    }

    public static Boolean getBoolean(final ResultSet rs, final String columnName) throws SQLException {
        return getBoolean(rs, rs.findColumn(columnName));
    }

    public static Byte getByte(final CallableStatement cs, final int index) throws SQLException {
        final byte value = cs.getByte(index);

        return cs.wasNull() ? null : value;
    }

    public static Byte getByte(final ResultSet rs, final int index) throws SQLException {
        final byte value = rs.getByte(index);

        return rs.wasNull() ? null : value;
    }

    public static Byte getByte(final ResultSet rs, final String columnName) throws SQLException {
        return getByte(rs, rs.findColumn(columnName));
    }

    public static String getDatabaseProductName(final DataSource dataSource) throws SQLException {
        return extractDatabaseMetaData(dataSource, dbMd -> {
            try {
                return dbMd.getDatabaseProductName();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static String getDatabaseProductVersion(final DataSource dataSource) throws SQLException {
        return extractDatabaseMetaData(dataSource, dbMd -> {
            try {
                return dbMd.getDatabaseProductVersion();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static Double getDouble(final CallableStatement cs, final int index) throws SQLException {
        final double value = cs.getDouble(index);

        return cs.wasNull() ? null : value;
    }

    public static Double getDouble(final ResultSet rs, final int index) throws SQLException {
        final double value = rs.getDouble(index);

        return rs.wasNull() ? null : value;
    }

    public static Double getDouble(final ResultSet rs, final String columnName) throws SQLException {
        return getDouble(rs, rs.findColumn(columnName));
    }

    public static Float getFloat(final CallableStatement cs, final int index) throws SQLException {
        final float value = cs.getFloat(index);

        return cs.wasNull() ? null : value;
    }

    public static Float getFloat(final ResultSet rs, final int index) throws SQLException {
        final float value = rs.getFloat(index);

        return rs.wasNull() ? null : value;
    }

    public static Float getFloat(final ResultSet rs, final String columnName) throws SQLException {
        return getFloat(rs, rs.findColumn(columnName));
    }

    public static Integer getInteger(final CallableStatement cs, final int index) throws SQLException {
        final int value = cs.getInt(index);

        return cs.wasNull() ? null : value;
    }

    public static Integer getInteger(final ResultSet rs, final int index) throws SQLException {
        final int value = rs.getInt(index);

        return rs.wasNull() ? null : value;
    }

    public static Integer getInteger(final ResultSet rs, final String columnName) throws SQLException {
        return getInteger(rs, rs.findColumn(columnName));
    }

    public static Long getLong(final CallableStatement cs, final int index) throws SQLException {
        final long value = cs.getLong(index);

        return cs.wasNull() ? null : value;
    }

    public static Long getLong(final ResultSet rs, final int index) throws SQLException {
        final long value = rs.getLong(index);

        return rs.wasNull() ? null : value;
    }

    public static Long getLong(final ResultSet rs, final String columnName) throws SQLException {
        return getLong(rs, rs.findColumn(columnName));
    }

    public static Short getShort(final CallableStatement cs, final int index) throws SQLException {
        final short value = cs.getShort(index);

        return cs.wasNull() ? null : value;
    }

    public static Short getShort(final ResultSet rs, final int index) throws SQLException {
        final short value = rs.getShort(index);

        return rs.wasNull() ? null : value;
    }

    public static Short getShort(final ResultSet rs, final String columnName) throws SQLException {
        return getShort(rs, rs.findColumn(columnName));
    }

    /**
     * Fügt zu der IN-Query die entsprechenden Werte hinzu.
     */
    public static StringBuilder parameterAsString(final Iterable<String> values, final char separator) {
        final StringBuilder builder = new StringBuilder();

        for (final Iterator<String> iter = values.iterator(); iter.hasNext(); ) {
            builder.append(iter.next());

            if (iter.hasNext()) {
                builder.append(separator);
            }
        }

        return builder;
    }

    /**
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    public static ObjectTable toObjectTable(final ResultSet resultSet) throws SQLException {
        Objects.requireNonNull(resultSet, "resultSet required");

        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        // Spaltennamen / Header
        final String[] header = new String[columnCount];

        for (int column = 1; column <= columnCount; column++) {
            header[column - 1] = metaData.getColumnLabel(column).toUpperCase();
        }

        final ObjectTable objectTable = new ObjectTable(header);

        // Daten
        while (resultSet.next()) {
            final Object[] row = new Object[columnCount];

            for (int column = 1; column <= columnCount; column++) {
                final Object obj = resultSet.getObject(column);
                final Object value;

                if (obj == null) {
                    value = "";
                }
                else if (obj instanceof byte[] bytes) {
                    value = new String(bytes, StandardCharsets.UTF_8);
                }
                else {
                    value = obj;
                }

                row[column - 1] = value;
            }

            objectTable.addRow(row);
        }

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }

        return objectTable;
    }

    public static ObjectTable toObjectTable(final ResultSetMetaData rsMeta) throws SQLException {
        Objects.requireNonNull(rsMeta, "resultSetMetaData required");

        // Spaltennamen / Header
        final String[] header = new String[5];
        header[0] = "ColumnName";
        header[1] = "ClassName";
        header[2] = "TypeName";
        header[3] = "Type";
        header[4] = "Nullable";

        final ObjectTable objectTable = new ObjectTable(header);

        // Daten
        for (int col = 1; col <= rsMeta.getColumnCount(); col++) {
            final Object[] row = new String[5];

            row[0] = rsMeta.getColumnName(col);
            row[1] = rsMeta.getColumnClassName(col);
            row[2] = rsMeta.getColumnTypeName(col);
            row[3] = String.valueOf(rsMeta.getColumnType(col));
            row[4] = String.valueOf(rsMeta.isNullable(col));

            objectTable.addRow(row);
        }

        return objectTable;
    }

    /**
     * Tabular printing the {@link ResultSet}.<br>
     * Stream is not closed.<br>
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    public static void write(final ResultSet resultSet, final PrintStream ps) throws SQLException {
        final ObjectTable objectTable = toObjectTable(resultSet);
        objectTable.writeStringTable(ps, '-', '|');

        // ResultSet wieder zurück auf Anfang.
        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }
    }

    /**
     * Tabular printing the {@link ResultSetMetaData}.
     */
    public static void write(final ResultSetMetaData rsMeta, final PrintStream ps) throws SQLException {
        final ObjectTable objectTable = toObjectTable(rsMeta);
        objectTable.writeStringTable(ps, '-', '|');
    }

    /**
     * Stream is not closed.<br>
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    public static void writeCsv(final ResultSet resultSet, final OutputStream outputStream) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        final IntFunction<String> headerFunction = column -> {
            try {
                return metaData.getColumnLabel(column + 1).toUpperCase();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        final BiFunction<Integer, Integer, String> dataFunction = (row, column) -> {
            try {
                final Object obj = resultSet.getObject(column + 1);
                final String value;

                if (obj instanceof byte[] bytes) {
                    value = new String(bytes, StandardCharsets.UTF_8);
                }
                else {
                    value = Objects.toString(obj, null);
                }

                return value;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        final IntPredicate finishPredicate = row -> {
            try {
                return resultSet.next();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        CsvUtils.writeCsv(outputStream, columnCount, headerFunction, dataFunction, finishPredicate);

        // Reset ResultSet.
        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }
    }

    /**
     * Erzeugt eine "in"- oder "not in"-Clause und berücksichtigt das bei Oracle nur max. 1000 Werte<br>
     * enthalten sein dürfen. Existieren mehr als 1000 Werte, werden diese mit einem or<br>
     * als weitere Clause angehängt.
     */
    private static void createInOrNotInClause(final String column, final StringBuilder sql, final Set<? extends Number> elements, final String inOrNotIn) {
        sql.append(column).append(" ").append(inOrNotIn).append(" (");

        if (elements == null || elements.isEmpty()) {
            sql.append("-1)");

            return;
        }

        final Iterator<? extends Number> iterator = elements.iterator();

        int i = 0;

        while (iterator.hasNext()) {
            final Number number = iterator.next();

            sql.append(number);
            i++;

            if ((i % 1000) == 0 && iterator.hasNext()) {
                // Neuen Block anfangen,
                sql.append(") or ").append(column).append(" ").append(inOrNotIn).append(" (");
            }
            else if (iterator.hasNext()) {
                sql.append(",");
            }
        }

        sql.append(")");
    }

    private JdbcUtils() {
        super();
    }
}
