package de.freese.base.core.blobstore.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
final class SqlBlobInputStream extends InputStream {
    private final java.sql.Blob blob;

    private final InputStream blobInputStream;

    private final Connection connection;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PreparedStatement prepareStatement;

    private final ResultSet resultSet;

    SqlBlobInputStream(Connection connection, PreparedStatement prepareStatement) throws SQLException {
        this.connection = Objects.requireNonNull(connection, "connection required");
        this.prepareStatement = Objects.requireNonNull(prepareStatement, "prepareStatement required");

        this.resultSet = prepareStatement.executeQuery();
        this.resultSet.next();

        this.blob = resultSet.getBlob("BLOB");
        this.blobInputStream = blob.getBinaryStream();
    }

    @Override
    public int available() throws IOException {
        return this.blobInputStream.available();
    }

    @Override
    public void close() throws IOException {
        super.close();

        SQLException sex = null;

        try {
            this.blobInputStream.close();
            this.blob.free();
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;
        }

        try {
            this.resultSet.close();
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;
        }

        try {
            this.prepareStatement.close();
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;
        }

        try {
            this.connection.close();
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;
        }

        if (sex != null) {
            throw new RuntimeException(sex);
        }
    }

    @Override
    public synchronized void mark(final int readLimit) {
        this.blobInputStream.mark(readLimit);
    }

    @Override
    public boolean markSupported() {
        return this.blobInputStream.markSupported();
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.blobInputStream.read(b, off, len);
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return this.blobInputStream.read(b);
    }

    @Override
    public int read() throws IOException {
        return this.blobInputStream.read();
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return this.blobInputStream.readAllBytes();
    }

    @Override
    public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
        return this.blobInputStream.readNBytes(b, off, len);
    }

    @Override
    public byte[] readNBytes(final int len) throws IOException {
        return this.blobInputStream.readNBytes(len);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.blobInputStream.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return this.blobInputStream.skip(n);
    }

    @Override
    public void skipNBytes(final long n) throws IOException {
        this.blobInputStream.skipNBytes(n);
    }

    @Override
    public long transferTo(final OutputStream out) throws IOException {
        return this.blobInputStream.transferTo(out);
    }

    private Logger getLogger() {
        return logger;
    }
}
