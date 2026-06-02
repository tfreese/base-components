package de.freese.base.core.rvs;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public final class RvsJdbcUtils {

    private RvsJdbcUtils() {
        super();
    }

    public static void bindFields(final PreparedStatement ps, final RvsRecord rvsRecord, final RvsLayout rvsLayout) throws SQLException {
        final List<RvsField> fields = rvsLayout.getFields();

        for (int index = 1; index <= fields.size(); index++) {
            final RvsField rvsField = fields.get(index - 1);
            final Object value = rvsRecord.getValue(rvsField.getName());

            setTyped(ps, index, value);
        }
    }

    private static void setTyped(final PreparedStatement ps, final int index, final Object value) throws SQLException {
        switch (value) {
            case null -> ps.setObject(index, null);
            case final String s -> ps.setString(index, s);
            case final Integer i -> ps.setInt(index, i);
            case final Long l -> ps.setLong(index, l);
            case final BigDecimal bd -> ps.setBigDecimal(index, bd);
            case final LocalDate ld -> ps.setDate(index, Date.valueOf(ld));
            case final java.util.Date d -> ps.setTimestamp(index, new Timestamp(d.getTime()));
            default -> ps.setObject(index, value);
        }
    }

    public static String createInsertSql(final String tableName, final RvsLayout rvsLayout) {
        final StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableName.toUpperCase()).append(" (");

        final Function<RvsField, String> fielNameToColumnName = rvsField -> {
            final String fieldName = rvsField.getName();
            final String columnName = fieldName.replace(' ', '_').replace('.', '_');

            return columnName.toUpperCase();
        };

        sql.append(String.join(", ", rvsLayout.getFields().stream().map(fielNameToColumnName).toList()));

        sql.append(") values (");

        sql.append(String.join(", ", rvsLayout.getFields().stream().map(rvsField -> "?").toList()));

        sql.append(")");

        return sql.toString();
    }
}
