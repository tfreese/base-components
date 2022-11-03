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
     */
    OutputStream create(BlobId id) throws Exception;

    void create(BlobId id, InputStream inputStream) throws Exception;

    void delete(BlobId id) throws Exception;

    boolean exists(BlobId id) throws Exception;

    Blob get(BlobId id) throws Exception;
}
