// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.freese.base.core.function.ThrowingConsumer;

/**
 * Referenz für binäre Daten aus einem {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface Blob
{
    /**
     * @param consumer {@link ThrowingConsumer}
     */
    void consumeInputStream(ThrowingConsumer<InputStream, Exception> consumer);

    /**
     * @return byte[]
     *
     * @throws IOException Falls was schiefgeht
     */
    default byte[] getAllBytes() throws IOException
    {
        byte[] bytes = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            consumeInputStream(inputStream -> inputStream.transferTo(baos));

            baos.flush();

            bytes = baos.toByteArray();
        }

        return bytes;
    }

    /**
     * @return {@link BlobId}
     */
    BlobId getId();

    /**
     * Liefert die Größe/Länge des Blobs in Byte.
     *
     * @return long
     */
    long getLength();
}
