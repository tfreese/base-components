package de.freese.base.core.blobstore.datasource;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

import javax.sql.DataSource;

import de.freese.base.core.blobstore.AbstractBlobStore;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
public class DatasourceBlobStore extends AbstractBlobStore
{
    /**
     *
     */
    private final DataSource dataSource;

    /**
     * @param dataSource {@link DataSource}
     */
    public DatasourceBlobStore(DataSource dataSource)
    {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    @Override
    public OutputStream create(final BlobId id) throws Exception
    {
        String sql = "insert into BLOB_STORE (URI, BLOB) values (?, ?)";

        Connection connection = this.dataSource.getConnection();
        PreparedStatement prepareStatement = connection.prepareStatement(sql);

        return new SqlBlobOutputStream(id.getUri(), connection, prepareStatement);
    }

    @Override
    public void create(final BlobId id, final InputStream inputStream) throws Exception
    {
        String sql = "insert into BLOB_STORE (URI, BLOB) values (?, ?)";

        try (Connection connection = this.dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql))
            {
                prepareStatement.setString(1, id.getUri().toString());
                prepareStatement.setBlob(2, inputStream);
                prepareStatement.executeUpdate();

                connection.commit();
            }
            catch (Exception ex)
            {
                connection.rollback();

                throw ex;
            }
        }
    }

    /**
     * @throws Exception Falls was schiefgeht
     */
    public void createDatabaseIfNotExist() throws Exception
    {
        boolean databaseExists = false;

        try (Connection connection = this.dataSource.getConnection())
        {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "BLOB_STORE", null);

            if (resultSet.next())
            {
                databaseExists = true;
            }
        }

        if (databaseExists)
        {
            return;
        }

        // LENGTH bigint not null,
        String createSql = "create table BLOB_STORE (URI varchar(1000) not null primary key, BLOB blob not null)";

        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute(createSql);
        }
    }

    @Override
    public void delete(final BlobId id) throws Exception
    {
        String sql = "delete from BLOB_STORE where URI = ?";

        try (Connection connection = this.dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            try (PreparedStatement prepareStatement = connection.prepareStatement(sql))
            {
                prepareStatement.setString(1, id.getUri().toString());
                prepareStatement.executeUpdate();

                connection.commit();
            }
            catch (Exception ex)
            {
                connection.rollback();

                throw ex;
            }
        }
    }

    @Override
    public boolean exists(final BlobId id) throws Exception
    {
        String sql = "select count(*) from BLOB_STORE where URI = ?";

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql))
        {
            prepareStatement.setString(1, id.getUri().toString());

            try (ResultSet resultSet = prepareStatement.executeQuery())
            {
                resultSet.next();

                int result = resultSet.getInt(1);

                return result > 0;
            }
        }
    }

    /**
     * @param id {@link BlobId}
     *
     * @return InputStream
     *
     * @throws Exception Falls was schiefgeht
     */
    InputStream inputStream(BlobId id) throws Exception
    {
        String sql = "select BLOB from BLOB_STORE where URI = ?";

        Connection connection = this.dataSource.getConnection();

        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        prepareStatement.setString(1, id.getUri().toString());

        return new SqlBlobInputStream(connection, prepareStatement);
    }

    /**
     * @param id {@link BlobId}
     *
     * @return long
     *
     * @throws Exception Falls was schiefgeht
     */
    long length(BlobId id) throws Exception
    {
        String sql = "select BLOB from BLOB_STORE where URI = ?";

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql))
        {
            prepareStatement.setString(1, id.getUri().toString());

            try (ResultSet resultSet = prepareStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    java.sql.Blob blob = resultSet.getBlob("BLOB");

                    return blob.length();
                }
            }
        }

        return 0;
    }

    @Override
    protected Blob doGet(final BlobId id) throws Exception
    {
        return new DatasourceBlob(id, this);
    }
}
