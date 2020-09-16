// Created: 11.01.2017
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * {@link OutputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitorOutputStream extends OutputStream
{
    /**
    *
    */
    private final OutputStream delegate;

    /**
    *
    */
    private final BiConsumer<Long, Long> monitor;

    /**
     * Anzahl Bytes (Größe) des gesamten Channels.
     */
    private final long size;

    /**
     * Anzahl geschriebene Bytes.
     */
    private long sizeWritten = 0;

    /**
     * Erzeugt eine neue Instanz von {@link MonitorOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param monitor {@link BiConsumer}; Erster Parameter = Anzahl geschriebene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitorOutputStream(final OutputStream delegate, final BiConsumer<Long, Long> monitor, final long size)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, () -> "delegate required");
        this.monitor = Objects.requireNonNull(monitor, () -> "monitor required");
        this.size = size;
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        this.delegate.flush();
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException
    {
        this.delegate.write(b);

        this.sizeWritten += b.length;

        this.monitor.accept(this.sizeWritten, this.size);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        this.delegate.write(b, off, len);

        this.sizeWritten += len;

        this.monitor.accept(this.sizeWritten, this.size);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        this.delegate.write(b);

        this.sizeWritten++;

        this.monitor.accept(this.sizeWritten, this.size);
    }
}
