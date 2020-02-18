/**
 * Created: 11.01.2017
 */

package de.freese.base.persistence.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * {@link ThreadLocal} für eine {@link Connection}.
 *
 * @author Thomas Freese
 */
public final class ConnectionHolder
{
    /**
     *
     */
    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Setzt autoCommit = false auf der aktuellen {@link Connection}.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    @SuppressWarnings("resource")
    public static void beginTX() throws SQLException
    {
        Connection connection = get();

        // ReadOnly Flag ändern geht nur ausserhalb einer TX.
        if (connection.isReadOnly())
        {
            connection.setReadOnly(false);
        }

        if (connection.getAutoCommit())
        {
            connection.setAutoCommit(false);
        }
    }

    /**
     * Ruft die Methode {@link Connection#close()} auf der aktuellen {@link Connection} auf.<br>
     * Die {@link Connection} wird anschliessend mit {@link #remove()} aus der {@link ThreadLocal} entfernt.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void close() throws SQLException
    {
        try (Connection connection = get())
        {
            connection.setAutoCommit(true);
            connection.setReadOnly(true); // ReadOnly Flag ändern geht nur ausserhalb einer TX.
        }

        remove();
    }

    /**
     * Ruft die Methode {@link Connection#commit()} auf der aktuellen {@link Connection} auf.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void commitTX() throws SQLException
    {
        get().commit();
    }

    /**
     * Liefert die {@link Connection} für den aktuellen Thread.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @return {@link Connection}
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    @SuppressWarnings("resource")
    public static Connection get() throws SQLException
    {
        Connection connection = THREAD_LOCAL.get();

        return Objects.requireNonNull(connection, "connection required, call #set(Connection) first");
    }

    /**
     * Liefert true, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @return boolean
     */
    public static boolean isEmpty()
    {
        return THREAD_LOCAL.get() == null;
    }

    /**
     * Entfernt die {@link Connection} für den aktuellen Thread.
     */
    public static void remove()
    {
        THREAD_LOCAL.remove();
    }

    /**
     * Ruft die Methode {@link Connection#rollback()} auf der aktuellen {@link Connection} auf.<br>
     * Wirft eine {@link NullPointerException}, wenn der aktuelle Thread keine {@link Connection} hat.
     *
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void rollbackTX() throws SQLException
    {
        get().rollback();
    }

    /**
     * Setzt die {@link Connection} für den aktuellen Thread.<br>
     * Wirft eine {@link IllegalStateException}, wenn der aktuelle Thread bereits eine {@link Connection} hat.
     *
     * @param connection {@link Connection}
     * @throws SQLException Falls was schief geht.
     * @see #isEmpty()
     * @see #set(Connection)
     */
    public static void set(final Connection connection) throws SQLException
    {
        if (THREAD_LOCAL.get() != null)
        {
            throw new IllegalStateException("connection already set, call #remove() first");
        }

        Objects.requireNonNull(connection, "connection required");

        THREAD_LOCAL.set(connection);
    }
}
