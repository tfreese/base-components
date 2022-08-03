package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Thomas Freese
 */
public class FakeOutStream extends OutputStream
{
    /**
     *
     */
    private volatile boolean closed;

    @Override
    public void close() throws IOException
    {
        closed = true;
    }

    @Override
    public void write(final int b) throws IOException
    {
        ensureOpen();
    }

    /**
     *
     */
    private void ensureOpen() throws IOException
    {
        if (closed)
        {
            throw new IOException("Stream closed");
        }
    }
}
