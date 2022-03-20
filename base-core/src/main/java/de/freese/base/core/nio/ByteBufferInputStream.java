// Created: 15.09.2020
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ByteBufferInputStream extends InputStream
{
    /**
     *
     */
    private final ByteBuffer buffer;

    /**
     * Erstellt ein neues {@link ByteBufferInputStream} Object.
     *
     * @param buffer {@link ByteBuffer}
     */
    public ByteBufferInputStream(final ByteBuffer buffer)
    {
        super();

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException
    {
        return this.buffer.remaining();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(final int readLimit)
    {
        this.buffer.mark();
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported()
    {
        return true;
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        if (this.buffer.hasRemaining())
        {
            return this.buffer.get() & 0xff;
        }

        return -1;
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        int remaining = this.buffer.remaining();

        if (remaining > 0)
        {
            int readBytes = Math.min(remaining, len);
            this.buffer.get(b, off, readBytes);

            return readBytes;
        }

        return -1;
    }

    /**
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException
    {
        this.buffer.reset();
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(final long n) throws IOException
    {
        int bytes;

        if (n > Integer.MAX_VALUE)
        {
            bytes = this.buffer.remaining();
        }
        else
        {
            bytes = Math.min(this.buffer.remaining(), (int) n);
        }

        this.buffer.position(this.buffer.position() + bytes);

        return bytes;
    }
}
