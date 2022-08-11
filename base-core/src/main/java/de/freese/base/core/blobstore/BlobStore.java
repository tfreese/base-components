// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface of a BlobStore.<br>
 * <a href="https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api">nexus-blobstore-api</a>
 *
 * @author Thomas Freese
 */
public interface BlobStore
{
    /**
     * <b>This Stream MUST be closed to avoid resource exhausting !</b>
     *
     * @param id {@link BlobId}
     *
     * @return OutputStream
     *
     * @throws Exception Falls was schiefgeht
     */
    OutputStream create(BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     * @param inputStream {@link InputStream}
     *
     * @throws Exception Falls was schiefgeht
     */
    void create(BlobId id, InputStream inputStream) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @throws Exception Falls was schiefgeht
     */
    void delete(BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @return boolean
     *
     * @throws Exception Falls was schiefgeht
     */
    boolean exists(BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     *
     * @return {@link Blob}
     *
     * @throws Exception Falls was schiefgeht
     */
    Blob get(BlobId id) throws Exception;
}
