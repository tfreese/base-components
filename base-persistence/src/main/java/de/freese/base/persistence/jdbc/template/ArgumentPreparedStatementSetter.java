// Created: 04.06.2018
package de.freese.base.persistence.jdbc.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import org.springframework.jdbc.core.SqlTypeValue;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.ArgumentPreparedStatementSetter<br>
 * jedoch ohne die Abhängigkeiten zum Spring-Framework.<br>
 *
 * @author Thomas Freese
 */
public class ArgumentPreparedStatementSetter implements PreparedStatementSetter
{
    /**
     * Constant that indicates an unknown (or unspecified) SQL type.
     *
     * @see java.sql.Types
     */
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    /**
     * @param ps {@link PreparedStatement}
     * @param paramIndex int
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private static void setNull(final PreparedStatement ps, final int paramIndex) throws SQLException
    {
        boolean useSetObject = false;
        int sqlTypeToUse = Types.NULL;

        // Proceed with database-specific checks
        DatabaseMetaData dbmd = ps.getConnection().getMetaData();
        String jdbcDriverName = dbmd.getDriverName();
        String databaseProductName = dbmd.getDatabaseProductName();

        if (databaseProductName.startsWith("Informix") || (jdbcDriverName.startsWith("Microsoft") && jdbcDriverName.contains("SQL Server")))
        {
            // "Microsoft SQL Server JDBC Driver 3.0" versus "Microsoft JDBC Driver 4.0 for SQL Server"
            useSetObject = true;
        }
        else if (databaseProductName.startsWith("DB2") || jdbcDriverName.startsWith("jConnect") || jdbcDriverName.startsWith("SQLServer")
                || jdbcDriverName.startsWith("Apache Derby"))
        {
            sqlTypeToUse = Types.VARCHAR;
        }

        if (useSetObject)
        {
            ps.setObject(paramIndex, null);
        }
        else
        {
            ps.setNull(paramIndex, sqlTypeToUse);
        }
    }

    /**
     * @param ps {@link PreparedStatement}
     * @param paramIndex int
     * @param value value
     * @throws SQLException if thrown by PreparedStatement methods
     * @see SqlTypeValue
     */
    public static void setParameterValue(final PreparedStatement ps, final int paramIndex, final Object value) throws SQLException
    {
        Object valueToUse = value;

        if (valueToUse == null)
        {
            setNull(ps, paramIndex);
        }
        else
        {
            setValue(ps, paramIndex, valueToUse);
        }
    }

    /**
     * @param ps {@link PreparedStatement}
     * @param paramIndex int
     * @param value Object
     * @throws SQLException Falls was schief geht.
     */
    private static void setValue(final PreparedStatement ps, final int paramIndex, final Object value) throws SQLException
    {
        if (value instanceof Boolean)
        {
            boolean booleanAsLong = false;

            if (booleanAsLong)
            {
                ps.setLong(paramIndex, ((Boolean) value) ? 1 : 0);
            }
            else
            {
                ps.setBoolean(paramIndex, (Boolean) value);
            }
        }
        else if (value instanceof BigDecimal)
        {
            ps.setBigDecimal(paramIndex, (BigDecimal) value);
        }
        else if (value instanceof Byte)
        {
            ps.setByte(paramIndex, (Byte) value);
        }
        else if (value instanceof Calendar)
        {
            Calendar calendar = (Calendar) value;
            ps.setDate(paramIndex, new java.sql.Date(calendar.getTime().getTime()), calendar);
        }
        else if (value instanceof java.sql.Date)
        {
            ps.setDate(paramIndex, (java.sql.Date) value);
        }
        else if (value instanceof java.util.Date)
        {
            ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) value).getTime()));
        }
        else if (value instanceof Double)
        {
            ps.setDouble(paramIndex, (Double) value);
        }
        else if (value instanceof Float)
        {
            ps.setFloat(paramIndex, (Float) value);
        }
        else if (value instanceof InputStream)
        {
            ps.setBinaryStream(paramIndex, (InputStream) value);
        }
        else if (value instanceof Integer)
        {
            ps.setInt(paramIndex, (Integer) value);
        }
        else if (value instanceof Long)
        {
            ps.setLong(paramIndex, (Long) value);
        }
        else if (value instanceof Short)
        {
            ps.setShort(paramIndex, (Short) value);
        }
        else if ((value instanceof CharSequence) || (value instanceof StringWriter))
        {
            String strVal = value.toString();

            if (strVal.length() > 4000)
            {
                ps.setClob(paramIndex, new StringReader(strVal), strVal.length());
            }
            else
            {
                ps.setString(paramIndex, strVal);
            }
        }
        else if (value instanceof java.sql.Timestamp)
        {
            ps.setTimestamp(paramIndex, (java.sql.Timestamp) value);
        }
        // Arrays
        else if (value instanceof byte[])
        {
            byte[] data = (byte[]) value;

            if (data.length == 0)
            {
                ps.setNull(paramIndex, Types.ARRAY);
            }
            else
            {
                try (InputStream inputStream = new ByteArrayInputStream(data))
                {
                    ps.setBinaryStream(paramIndex, inputStream);
                }
                catch (IOException ioex)
                {
                    throw new SQLException(ioex);
                }
            }
        }
        else if (value instanceof double[])
        {
            double[] data = (double[]) value;

            if (data.length == 0)
            {
                ps.setNull(paramIndex, Types.ARRAY);
            }
            else
            {
                ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);
                DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
                doubleBuffer.put(data);

                byte[] bytes = byteBuffer.array();

                try (InputStream inputStream = new ByteArrayInputStream(bytes))
                {
                    ps.setBinaryStream(paramIndex, inputStream);
                }
                catch (IOException ioex)
                {
                    throw new SQLException(ioex);
                }
            }
        }
        else if (value instanceof int[])
        {
            int[] data = (int[]) value;

            if (data.length == 0)
            {
                ps.setNull(paramIndex, Types.ARRAY);
            }
            else
            {
                // try (DataOutputStream dos = new DataOutputStream(new ByteArrayOutputStream()))
                // {
                // dos.writeInt(v);
                // }
                // catch (IOException ioex)
                // {
                // throw new SQLException(ioex);
                // }

                ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(data);

                byte[] bytes = byteBuffer.array();

                // ByteBuffer buffer = ByteBuffer.wrap(bytes);
                // return buffer.getInt();
                //
                // IntBuffer intBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                // int[] array = new int[intBuf.remaining()];
                // intBuf.get(array);

                try (InputStream inputStream = new ByteArrayInputStream(bytes))
                {
                    ps.setBinaryStream(paramIndex, inputStream);
                }
                catch (IOException ioex)
                {
                    throw new SQLException(ioex);
                }
            }

            // if (((int[]) value).length == 0)
            // {
            // ps.setNull(paramIndex, Types.ARRAY, ARRAY_TYPE_NAME);
            // }
            // else
            // {
            // ps.setArray(paramIndex, ps.getConnection().unwrap(OracleConnection.class).createOracleArray(ARRAY_TYPE_NAME, param));
            // }
        }
        else if (value instanceof long[])
        {
            long[] data = (long[]) value;
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);
            LongBuffer longBuffer = byteBuffer.asLongBuffer();
            longBuffer.put(data);

            byte[] bytes = byteBuffer.array();

            try (InputStream inputStream = new ByteArrayInputStream(bytes))
            {
                ps.setBinaryStream(paramIndex, inputStream);
            }
            catch (IOException ioex)
            {
                throw new SQLException(ioex);
            }
        }
        else
        {
            ps.setObject(paramIndex, value);
        }
    }

    /**
     *
     */
    private final Object[] args;

    /**
     * Erzeugt eine neue Instanz von {@link ArgumentPreparedStatementSetter}.
     *
     * @param args Object[]
     */
    public ArgumentPreparedStatementSetter(final Object[] args)
    {
        super();

        this.args = args;
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter#setValues(java.sql.PreparedStatement)
     */
    @Override
    public void setValues(final PreparedStatement preparedStatement) throws SQLException
    {
        if (this.args == null)
        {
            return;
        }

        for (int i = 0; i < this.args.length; i++)
        {
            Object arg = this.args[i];

            setParameterValue(preparedStatement, i + 1, arg);
        }
    }
}
