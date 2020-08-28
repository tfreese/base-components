// Created: 28.08.2020
package de.freese.base.core.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * ByteArrayOutputStream mit direkten Zugriff auf das ByteArray ohne es zu kopieren.
 *
 * @author Thomas Freese
 */
public class SharedByteArrayOutputStream extends ByteArrayOutputStream
{
    /**
     * Erstellt ein neues {@link SharedByteArrayOutputStream} Object.
     */
    public SharedByteArrayOutputStream()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link SharedByteArrayOutputStream} Object.
     *
     * @param size int
     */
    public SharedByteArrayOutputStream(final int size)
    {
        super(size);
    }

    /**
     * @return byte[]
     */
    public byte[] getArray()
    {
        return this.buf;
    }

    /**
     * @return {@link InputStream}
     */
    public InputStream toStream()
    {
        return new SharedByteArrayInputStream(this.buf, 0, this.count);
    }
}
