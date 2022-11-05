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

import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;

/**
 * Inspired by org.springframework.jdbc.core<br>
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
     * @see SqlTypeValue
     */
    public static void setParameterValue(final PreparedStatement ps, final int paramIndex, final Object value) throws SQLException
    {
        if (value == null)
        {
            setNull(ps, paramIndex);
        }
        else
        {
            setValue(ps, paramIndex, value);
        }
    }

    private static void setNull(final PreparedStatement ps, final int paramIndex) throws SQLException
    {
        boolean useSetObject = false;
        int sqlTypeToUse = Types.NULL;

        // Proceed with database-specific checks
        DatabaseMetaData metaData = ps.getConnection().getMetaData();
        String jdbcDriverName = metaData.getDriverName();
        String databaseProductName = metaData.getDatabaseProductName();

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

    private static void setValue(final PreparedStatement ps, final int paramIndex, final Object value) throws SQLException
    {
        if (value instanceof Boolean data)
        {
            boolean booleanAsLong = false;

            if (booleanAsLong)
            {
                ps.setLong(paramIndex, data ? 1L : 0L);
            }
            else
            {
                ps.setBoolean(paramIndex, data);
            }
        }
        else if (value instanceof BigDecimal data)
        {
            ps.setBigDecimal(paramIndex, data);
        }
        else if (value instanceof Byte data)
        {
            ps.setByte(paramIndex, data);
        }
        else if (value instanceof Calendar data)
        {
            ps.setDate(paramIndex, new java.sql.Date(data.getTime().getTime()), data);
        }
        else if (value instanceof java.sql.Date data)
        {
            ps.setDate(paramIndex, data);
        }
        else if (value instanceof java.util.Date data)
        {
            ps.setDate(paramIndex, new java.sql.Date(data.getTime()));
        }
        else if (value instanceof Double data)
        {
            ps.setDouble(paramIndex, data);
        }
        else if (value instanceof Float data)
        {
            ps.setFloat(paramIndex, data);
        }
        else if (value instanceof InputStream data)
        {
            ps.setBinaryStream(paramIndex, data);
        }
        else if (value instanceof Integer data)
        {
            ps.setInt(paramIndex, data);
        }
        else if (value instanceof Long data)
        {
            ps.setLong(paramIndex, data);
        }
        else if (value instanceof Short data)
        {
            ps.setShort(paramIndex, data);
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
        else if (value instanceof java.sql.Timestamp data)
        {
            ps.setTimestamp(paramIndex, data);
        }
        // Arrays
        else if (value instanceof byte[] data)
        {
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
                catch (IOException ex)
                {
                    throw new SQLException(ex);
                }
            }
        }
        else if (value instanceof double[] data)
        {
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
                catch (IOException ex)
                {
                    throw new SQLException(ex);
                }
            }
        }
        else if (value instanceof int[] data)
        {
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
                // catch (IOException ex)
                // {
                // throw new SQLException(ex);
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
                catch (IOException ex)
                {
                    throw new SQLException(ex);
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
        else if (value instanceof long[] data)
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);
            LongBuffer longBuffer = byteBuffer.asLongBuffer();
            longBuffer.put(data);

            byte[] bytes = byteBuffer.array();

            try (InputStream inputStream = new ByteArrayInputStream(bytes))
            {
                ps.setBinaryStream(paramIndex, inputStream);
            }
            catch (IOException ex)
            {
                throw new SQLException(ex);
            }
        }
        else
        {
            ps.setObject(paramIndex, value);
        }
    }

    private final Object[] args;

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
