// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.InputStream;

/**
 * Reference for binary Data from a {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface Blob
{
    /**
     * @return byte[]
     *
     * @throws Exception Falls was schiefgeht
     */
    default byte[] getAllBytes() throws Exception
    {
        try (InputStream inputStream = getInputStream())
        {
            return inputStream.readAllBytes();
        }
        
        //        byte[] bytes = null;
        //
        //        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //             InputStream inputStream = getInputStream())
        //        {
        //            inputStream.transferTo(baos);
        //
        //            baos.flush();
        //
        //            bytes = baos.toByteArray();
        //        }
        //
        //        return bytes;
    }

    /**
     * @return {@link BlobId}
     *
     * @throws Exception Falls was schiefgeht
     */
    BlobId getId() throws Exception;

    /**
     * <b>This Stream MUST be closed to avoid resource exhausting !</b>
     *
     * @return InputStream
     *
     * @throws Exception Falls was schiefgeht
     */
    InputStream getInputStream() throws Exception;

    /**
     * Blob length in Byte.
     *
     * @return long
     *
     * @throws Exception Falls was schiefgeht
     */
    long getLength() throws Exception;
}
