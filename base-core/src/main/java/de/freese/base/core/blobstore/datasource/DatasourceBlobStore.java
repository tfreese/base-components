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
import de.freese.base.core.function.ThrowingConsumer;

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

        String createSql = "create BLOB_STORE (URI varchar not null primary key, LENGTH bigint not null, BLOB blob not null)";

        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute(createSql);
        }
    }

    @Override
    protected void doCreate(final BlobId id, final ThrowingConsumer<OutputStream, Exception> consumer) throws Exception
    {
        // TODO
    }

    @Override
    protected void doCreate(final BlobId id, final InputStream inputStream) throws Exception
    {
        // TODO
    }

    @Override
    protected void doDelete(final BlobId id) throws Exception
    {
        String sql = "delete from BLOB_STORE where URI = ?";

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql))
        {
            prepareStatement.setString(1, id.getUri().toString());

            prepareStatement.executeUpdate();
        }
    }

    @Override
    protected boolean doExists(final BlobId id) throws Exception
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

    @Override
    protected Blob doGet(final BlobId id) throws Exception
    {
        return new DatasourceBlob(id, this);
    }
}
