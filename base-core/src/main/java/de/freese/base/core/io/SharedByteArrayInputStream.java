// Created: 28.08.2020
package de.freese.base.core.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Thomas Freese
 */
public class SharedByteArrayInputStream extends ByteArrayInputStream
{
    /**
     *
     */
    private int startIndex;

    /**
     * Erstellt ein neues {@link SharedByteArrayInputStream} Object.
     *
     * @param buf byte[]
     */
    public SharedByteArrayInputStream(final byte[] buf)
    {
        super(buf);
    }

    /**
     * Erstellt ein neues {@link SharedByteArrayInputStream} Object.
     *
     * @param buf byte[]
     * @param offset int
     * @param length int
     */
    public SharedByteArrayInputStream(final byte[] buf, final int offset, final int length)
    {
        super(buf, offset, length);

        this.startIndex = offset;
    }

    /**
     * @return int
     */
    public int getStartIndex()
    {
        return this.startIndex;
    }

    /**
     * Kapselt das interne ByteArray.
     *
     * @return {@link ByteBuffer}
     */
    public ByteBuffer toByteBuffer()
    {
        return ByteBuffer.wrap(this.buf, 0, this.count);
    }

    /**
     * @param start long
     * @param end long
     * @return {@link InputStream}
     */
    public InputStream toStream(final long start, long end)
    {
        if (start < 0)
        {
            throw new IllegalArgumentException("start < 0");
        }

        if (end == -1)
        {
            end = this.count - (long) this.startIndex;
        }

        return new SharedByteArrayInputStream(this.buf, this.startIndex + (int) start, (int) (end - start));
    }
}
