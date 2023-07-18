package de.freese.base.core.blobstore.datasource;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
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
final class SqlBlobInputStream extends FilterInputStream {
    private final java.sql.Blob blob;

    private final Connection connection;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PreparedStatement prepareStatement;

    private final ResultSet resultSet;

    SqlBlobInputStream(Connection connection, PreparedStatement prepareStatement) throws SQLException {
        super(null);

        this.connection = Objects.requireNonNull(connection, "connection required");
        this.prepareStatement = Objects.requireNonNull(prepareStatement, "prepareStatement required");

        this.resultSet = prepareStatement.executeQuery();
        this.resultSet.next();

        this.blob = resultSet.getBlob("BLOB");
        this.in = blob.getBinaryStream();
    }

    @Override
    public void close() throws IOException {
        SQLException sex = null;

        try {
            super.close();

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

    private InputStream getDelegate() {
        return this.in;
    }

    private Logger getLogger() {
        return logger;
    }
}
