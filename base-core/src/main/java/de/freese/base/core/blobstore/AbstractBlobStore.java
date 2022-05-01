// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;
import java.io.OutputStream;

import de.freese.base.core.function.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link BlobStore}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlobStore implements BlobStore
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void create(final BlobId id, final ThrowingConsumer<OutputStream, Exception> consumer)
    {
        try
        {
            doCreate(id, consumer);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    @Override
    public void create(final BlobId id, final InputStream inputStream)
    {
        try
        {
            doCreate(id, inputStream);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    @Override
    public void delete(final BlobId id)
    {
        try
        {
            doDelete(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    @Override
    public boolean exists(final BlobId id)
    {
        try
        {
            return doExists(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return false;
    }

    @Override
    public Blob get(final BlobId id)
    {
        if (!exists(id))
        {
            return null;
        }

        try
        {
            return doGet(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }

    /**
     * @param id {@link BlobId}
     * @param consumer {@link ThrowingConsumer}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract void doCreate(final BlobId id, ThrowingConsumer<OutputStream, Exception> consumer) throws Exception;

    /**
     * @param id {@link BlobId}
     * @param inputStream {@link InputStream}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract void doCreate(final BlobId id, final InputStream inputStream) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract void doDelete(final BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @return boolean
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract boolean doExists(final BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @return {@link Blob}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract Blob doGet(final BlobId id) throws Exception;

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
