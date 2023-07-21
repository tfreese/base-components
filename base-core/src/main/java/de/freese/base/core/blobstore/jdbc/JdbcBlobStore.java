package de.freese.base.core.blobstore.jdbc;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import javax.sql.DataSource;

import de.freese.base.core.blobstore.AbstractBlobStore;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
public class JdbcBlobStore extends AbstractBlobStore {

    private final DataSource dataSource;

    public JdbcBlobStore(DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    @Override
    public OutputStream create(final BlobId id) throws Exception {
        String sql = "insert into BLOB_STORE (URI, BLOB) values (?, ?)";

        Connection connection = getDataSource().getConnection();
        connection.setAutoCommit(false);

        java.sql.Blob blob = connection.createBlob();

        return new FilterOutputStream(blob.setBinaryStream(1)) {
            @Override
            public void close() throws IOException {
                super.close();

                SQLException exception = null;

                try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                    prepareStatement.setString(1, id.getUri().toString());
                    prepareStatement.setBlob(2, blob);
                    prepareStatement.executeUpdate();

                    connection.commit();
                }
                catch (SQLException ex) {
                    getLogger().error("Connection.commit: " + ex.getMessage(), ex);
                    exception = ex;

                    try {
                        connection.rollback();
                    }
                    catch (SQLException ex1) {
                        getLogger().error("Connection.rollback: " + ex1.getMessage(), ex1);
                        exception = ex1;
                    }
                }

                try {
                    blob.free();
                }
                catch (SQLException ex) {
                    getLogger().error("Blob.free: " + ex.getMessage(), ex);
                    exception = ex;
                }

                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    getLogger().error("Connection.close: " + ex.getMessage(), ex);
                    exception = ex;
                }

                if (exception != null) {
                    throw new IOException(exception);
                }
            }
        };
    }

    @Override
    public void create(final BlobId id, final InputStream inputStream) throws Exception {
        String sql = "insert into BLOB_STORE (URI, BLOB) values (?, ?)";

        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.setString(1, id.getUri().toString());
                prepareStatement.setBlob(2, inputStream);
                prepareStatement.executeUpdate();

                connection.commit();
            }
            catch (Exception ex) {
                connection.rollback();

                throw ex;
            }
        }
    }

    public void createDatabaseIfNotExist() throws Exception {
        boolean databaseExists = false;

        try (Connection connection = getDataSource().getConnection()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "BLOB_STORE", null);

            if (resultSet.next()) {
                databaseExists = true;
            }
        }

        if (databaseExists) {
            return;
        }

        // LENGTH bigint not null,
        String createSql = "create table BLOB_STORE (URI varchar(1000) not null primary key, BLOB blob not null)";

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createSql);
        }
    }

    @Override
    public void delete(final BlobId id) throws Exception {
        String sql = "delete from BLOB_STORE where URI = ?";

        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.setString(1, id.getUri().toString());
                prepareStatement.executeUpdate();

                connection.commit();
            }
            catch (Exception ex) {
                connection.rollback();

                throw ex;
            }
        }
    }

    @Override
    public boolean exists(final BlobId id) throws Exception {
        String sql = "select count(*) from BLOB_STORE where URI = ?";

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setString(1, id.getUri().toString());

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                resultSet.next();

                int result = resultSet.getInt(1);

                return result > 0;
            }
        }
    }

    InputStream inputStream(BlobId id) throws Exception {
        String sql = "select BLOB from BLOB_STORE where URI = ?";

        Connection connection = getDataSource().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id.getUri().toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return InputStream.nullInputStream();
        }

        java.sql.Blob blob = resultSet.getBlob("BLOB");

        return new FilterInputStream(blob.getBinaryStream()) {
            @Override
            public void close() throws IOException {
                super.close();

                SQLException exception = null;

                try {
                    super.close();

                    blob.free();
                }
                catch (SQLException ex) {
                    getLogger().error("Blob.free: " + ex.getMessage(), ex);
                    exception = ex;
                }

                try {
                    resultSet.close();
                }
                catch (SQLException ex) {
                    getLogger().error("ResultSet.close: " + ex.getMessage(), ex);
                    exception = ex;
                }

                try {
                    preparedStatement.close();
                }
                catch (SQLException ex) {
                    getLogger().error("PreparedStatement.close: " + ex.getMessage(), ex);
                    exception = ex;
                }

                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    getLogger().error("Connection.close: " + ex.getMessage(), ex);
                    exception = ex;
                }

                if (exception != null) {
                    throw new IOException(exception);
                }
            }
        };
    }

    long length(BlobId id) throws Exception {
        String sql = "select BLOB from BLOB_STORE where URI = ?";

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
            prepareStatement.setString(1, id.getUri().toString());

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    java.sql.Blob blob = resultSet.getBlob("BLOB");

                    return blob.length();
                }
            }
        }

        return -1;
    }

    @Override
    protected Blob doGet(final BlobId id) throws Exception {
        return new JdbcBlob(id, this);
    }

    protected DataSource getDataSource() {
        return dataSource;
    }
}
