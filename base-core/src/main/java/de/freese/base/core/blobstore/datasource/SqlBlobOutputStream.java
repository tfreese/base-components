package de.freese.base.core.blobstore.datasource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
final class SqlBlobOutputStream extends OutputStream
{
    /**
     *
     */
    private final java.sql.Blob blob;
    /**
     *
     */
    private final OutputStream blobOutputStream;
    /**
     *
     */
    private final Connection connection;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final PreparedStatement prepareStatement;
    /**
     *
     */
    private final URI uri;

    /**
     * @param uri {@link URI}
     * @param connection {@link Connection}
     * @param prepareStatement {@link PreparedStatement}
     *
     * @throws SQLException Falls was schiefgeht
     */
    SqlBlobOutputStream(URI uri, final Connection connection, final PreparedStatement prepareStatement) throws SQLException
    {
        this.uri = Objects.requireNonNull(uri, "uri required");

        this.connection = Objects.requireNonNull(connection, "connection required");
        this.prepareStatement = Objects.requireNonNull(prepareStatement, "prepareStatement required");

        this.blob = connection.createBlob();
        this.blobOutputStream = blob.setBinaryStream(1);
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        SQLException sex = null;

        try
        {
            this.blobOutputStream.close();

            this.prepareStatement.setString(1, this.uri.toString());
            this.prepareStatement.setBlob(2, this.blob);
            this.prepareStatement.executeUpdate();

            this.connection.commit();

            this.blob.free();
        }
        catch (SQLException ex)
        {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;

            try
            {
                this.connection.rollback();
            }
            catch (SQLException ex2)
            {
                getLogger().error(ex.getMessage(), ex);
                sex = ex;
            }
        }

        try
        {
            this.connection.close();
        }
        catch (SQLException ex)
        {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;
        }

        if (sex != null)
        {
            throw new RuntimeException(sex);
        }
    }

    @Override
    public void flush() throws IOException
    {
        this.blobOutputStream.flush();
    }

    @Override
    public void write(final byte[] b) throws IOException
    {
        this.blobOutputStream.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        this.blobOutputStream.write(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException
    {
        this.blobOutputStream.write(b);
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return logger;
    }
}
