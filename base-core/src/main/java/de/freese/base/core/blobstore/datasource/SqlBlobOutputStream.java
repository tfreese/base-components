package de.freese.base.core.blobstore.datasource;

import java.io.FilterOutputStream;
import java.io.IOException;
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
final class SqlBlobOutputStream extends FilterOutputStream {
    private final java.sql.Blob blob;

    private final Connection connection;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PreparedStatement prepareStatement;

    private final URI uri;

    SqlBlobOutputStream(URI uri, final Connection connection, final PreparedStatement prepareStatement) throws SQLException {
        super(null);

        this.uri = Objects.requireNonNull(uri, "uri required");

        this.connection = Objects.requireNonNull(connection, "connection required");
        this.prepareStatement = Objects.requireNonNull(prepareStatement, "prepareStatement required");

        this.blob = connection.createBlob();
        this.out = blob.setBinaryStream(1);
    }

    @Override
    public void close() throws IOException {

        SQLException sex = null;

        try {
            super.close();

            this.prepareStatement.setString(1, this.uri.toString());
            this.prepareStatement.setBlob(2, this.blob);
            this.prepareStatement.executeUpdate();

            this.connection.commit();

            this.blob.free();
        }
        catch (SQLException ex) {
            getLogger().error(ex.getMessage(), ex);
            sex = ex;

            try {
                this.connection.rollback();
            }
            catch (SQLException ex2) {
                getLogger().error(ex.getMessage(), ex);
                sex = ex;
            }
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

    private Logger getLogger() {
        return logger;
    }
}
