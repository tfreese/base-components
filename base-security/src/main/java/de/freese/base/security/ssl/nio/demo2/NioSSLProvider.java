package de.freese.base.security.ssl.nio.demo2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;

/**
 * @author Thomas Freese
 */
public abstract class NioSSLProvider extends SSLProvider
{
    /**
     *
     */
    private final ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);

    /**
    *
    */
    private final SelectionKey key;

    /**
     * Erstellt ein neues {@link NioSSLProvider} Object.
     *
     * @param key {@link SelectionKey}
     * @param engine {@link SSLEngine}
     * @param bufferSize int
     * @param ioWorker {@link Executor}
     * @param taskWorkers {@link Executor}
     */
    public NioSSLProvider(final SelectionKey key, final SSLEngine engine, final int bufferSize, final Executor ioWorker, final Executor taskWorkers)
    {
        super(engine, bufferSize, ioWorker, taskWorkers);

        this.key = key;
    }

    /**
     * @see de.freese.base.security.ssl.nio.demo2.SSLProvider#onOutput(java.nio.ByteBuffer)
     */
    @Override
    public void onOutput(final ByteBuffer encrypted)
    {
        try
        {
            ((WritableByteChannel) this.key.channel()).write(encrypted);
        }
        catch (IOException exc)
        {
            throw new IllegalStateException(exc);
        }
    }

    /**
     * @return boolean
     */
    public boolean processInput()
    {
        this.buffer.clear();
        int bytes;

        try
        {
            bytes = ((ReadableByteChannel) this.key.channel()).read(this.buffer);
        }
        catch (IOException ex)
        {
            bytes = -1;
        }

        if (bytes == -1)
        {
            return false;
        }

        this.buffer.flip();
        ByteBuffer copy = ByteBuffer.allocate(bytes);
        copy.put(this.buffer);
        copy.flip();
        this.notify(copy);

        return true;
    }
}