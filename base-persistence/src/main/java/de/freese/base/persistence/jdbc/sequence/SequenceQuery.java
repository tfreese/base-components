/**
 * Created: 04.02.2017
 */

package de.freese.base.persistence.jdbc.sequence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Random;
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
public interface SequenceQuery extends Function<String, String>
{
    /**
     *
     */
    static final Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Ermittelt anhand der {@link DatabaseMetaData} das passende SQL.
     *
     * @param connection {@link Connection}
     * @return {@link SequenceQuery}
     * @throws SQLException Falls was schief geht.
     */
    public static SequenceQuery determineQuery(final Connection connection) throws SQLException
    {
        DatabaseMetaData dbmd = connection.getMetaData();

        String product = dbmd.getDatabaseProductName().toLowerCase();
        product = product.split(" ")[0];
        // int majorVersion = dbmd.getDatabaseMajorVersion();
        // int minorVersion = dbmd.getDatabaseMinorVersion();

        SequenceQuery query = null;

        switch (product)
        {
            case "oracle":
                query = seq -> "select " + seq + ".nextval from dual";
                break;
            case "hsql":
                query = seq -> "call next value for " + seq;
                break;
            case "sqlite":
                query = seq -> "select random()"; // "SELECT ABS(RANDOM() - 1)";
                break;
            // case "mysql":
            // // CREATE TABLE sequence (id INT NOT NULL);
            // // INSERT INTO sequence VALUES (0);
            //
            // return seq -> "UPDATE sequence SET id=LAST_INSERT_ID(id + 1); SELECT LAST_INSERT_ID();";
            // break;

            default:
                // query = seq -> "select nvl(max(id), 0) + 1 from " + seq;
                query = seq -> "select count(*) + 1 from " + seq;
        }

        return query;
    }
}
