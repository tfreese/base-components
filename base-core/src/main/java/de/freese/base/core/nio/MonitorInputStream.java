// Created: 11.01.2017
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import javax.swing.ProgressMonitorInputStream;

/**
 * {@link InputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @see ProgressMonitorInputStream
 * @author Thomas Freese
 */
public class MonitorInputStream extends InputStream
{
    /**
    *
    */
    private long bytesRead = 0;

    /**
       *
       */
    private final LongConsumer bytesReadConsumer;

    /**
    *
    */
    private final InputStream delegate;

    /**
     * Erzeugt eine neue Instanz von {@link MonitorInputStream}
     *
     * @param delegate {@link InputStream}
     * @param bytesReadConsumer {@link BiConsumer}; Erster Parameter = Anzahl gelesene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitorInputStream(final InputStream delegate, final BiConsumer<Long, Long> bytesReadConsumer, final long size)
    {
        this(delegate, bytesRead -> bytesReadConsumer.accept(bytesRead, size));
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitorInputStream}
     *
     * @param delegate {@link InputStream}
     * @param bytesReadConsumer {@link LongConsumer}
     */
    public MonitorInputStream(final InputStream delegate, final LongConsumer bytesReadConsumer)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesReadConsumer = Objects.requireNonNull(bytesReadConsumer, "bytesReadConsumer required");
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException
    {
        return this.delegate.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public synchronized void mark(final int readlimit)
    {
        this.delegate.mark(readlimit);
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported()
    {
        return this.delegate.markSupported();
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        int read = this.delegate.read();

        this.bytesRead++;

        this.bytesReadConsumer.accept(this.bytesRead);

        return read;
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        int readCount = this.delegate.read(b);

        if (readCount > 0)
        {
            this.bytesRead += readCount;

            this.bytesReadConsumer.accept(this.bytesRead);
        }

        return readCount;
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        int readCount = this.delegate.read(b, off, len);

        if (readCount > 0)
        {
            this.bytesRead += readCount;

            this.bytesReadConsumer.accept(this.bytesRead);
        }

        return readCount;
    }

    /**
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException
    {
        this.delegate.reset();

        this.bytesRead -= this.delegate.available();

        this.bytesReadConsumer.accept(this.bytesRead);
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(final long n) throws IOException
    {
        long readCount = this.delegate.skip(n);

        if (readCount > 0)
        {
            this.bytesRead += readCount;

            this.bytesReadConsumer.accept(this.bytesRead);
        }

        return readCount;
    }
}
