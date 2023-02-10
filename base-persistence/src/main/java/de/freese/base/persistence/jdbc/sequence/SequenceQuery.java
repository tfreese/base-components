// Created: 04.02.2017
package de.freese.base.persistence.jdbc.sequence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Liefert die Query für eine Sequence.<br>
 * Beispiel:<br>
 *
 * <pre>
 * - Oracle: select SEQUENCE.nextval from dual
 * - HSQLDB: call next value for SEQUENCE
 * - Default: select count(*) + 1 from TABLE; select nvl(max(id), 0) + 1 from TABLE
 * </pre>
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SequenceQuery extends Function<String, String> {
    /**
     * Ermittelt anhand der {@link DatabaseMetaData} das passende SQL.
     */
    static SequenceQuery determineQuery(final Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        String product = metaData.getDatabaseProductName().toLowerCase();
        product = product.split(" ")[0];
        // int majorVersion = metaData.getDatabaseMajorVersion();
        // int minorVersion = metaData.getDatabaseMinorVersion();

        return switch (product) {
            case "oracle" -> seq -> "select " + seq + ".nextval from dual";
            case "hsql" -> seq -> "call next value for " + seq;
            case "sqlite" -> seq -> "select random()"; // "SELECT ABS(RANDOM() - 1)";
            default -> throw new IllegalArgumentException("Unexpected value: " + product);
        };
    }
}
