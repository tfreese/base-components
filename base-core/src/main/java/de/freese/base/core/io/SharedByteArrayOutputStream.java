// Created: 28.08.2020
package de.freese.base.core.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ByteArrayOutputStream mit direkten Zugriff auf das ByteArray über einen {@link ByteBuffer} ohne es zu kopieren.
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
     * Kapselt das ByteArray.
     *
     * @return {@link ByteBuffer}
     */
    public ByteBuffer toByteBuffer()
    {
        return ByteBuffer.wrap(this.buf, 0, this.count);
    }

    /**
     * @return {@link InputStream}
     */
    public InputStream toStream()
    {
        return new SharedByteArrayInputStream(this.buf, 0, this.count);
    }

    /**
     * @param buffer {@link ByteBuffer}
     */
    public void write(final ByteBuffer buffer)
    {
        write(buffer, buffer.remaining());
    }

    /**
     * @param buffer {@link ByteBuffer}
     * @param length int
     */
    public void write(final ByteBuffer buffer, final int length)
    {
        byte[] data = new byte[length];
        buffer.get(data);

        writeBytes(data);
    }
}
