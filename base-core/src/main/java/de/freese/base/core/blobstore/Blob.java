// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;

/**
 * Referenz für binäre Daten aus einem {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface Blob
{
    /**
     * @return {@link BlobId}
     */
    BlobId getId();

    /**
     * @return {@link InputStream}; unbuffered
     */
    InputStream getInputStream();

    /**
     * Liefert die Größe/Länge des Blobs in Byte.
     *
     * @return long
     */
    long getLength();
}
