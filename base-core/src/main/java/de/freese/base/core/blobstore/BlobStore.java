// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;
import java.io.OutputStream;

import de.freese.base.core.function.ThrowingConsumer;

/**
 * Interface eines BlobStores.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface BlobStore
{
    /**
     * @param id {@link BlobId}
     * @param consumer {@link ThrowingConsumer}
     */
    void create(BlobId id, ThrowingConsumer<OutputStream, Exception> consumer);

    /**
     * @param id {@link BlobId}
     * @param inputStream {@link InputStream}
     */
    void create(BlobId id, InputStream inputStream);

    /**
     * @param id {@link BlobId}
     */
    void delete(BlobId id);

    /**
     * @param id {@link BlobId}
     *
     * @return boolean
     */
    boolean exists(BlobId id);

    /**
     * @param id {@link BlobId}
     *
     * @return {@link Blob}
     */
    Blob get(BlobId id);
}
