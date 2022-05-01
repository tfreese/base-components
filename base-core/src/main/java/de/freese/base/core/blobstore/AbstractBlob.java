// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link Blob}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlob implements Blob
{
    /**
     *
     */
    private final BlobId id;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractBlob} Object.
     *
     * @param id {@link BlobId}
     */
    protected AbstractBlob(final BlobId id)
    {
        super();

        this.id = Objects.requireNonNull(id, "id required");
    }

    @Override
    public final BlobId getId()
    {
        return this.id;
    }

    @Override
    public InputStream getInputStream()
    {
        try
        {
            return doGetInputStream();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }

    @Override
    public long getLength()
    {
        try
        {
            return doGetLength();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return -1L;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return getId().toString();
    }

    /**
     * @return {@link InputStream}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract InputStream doGetInputStream() throws Exception;

    /**
     * @return long
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract long doGetLength() throws Exception;

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
