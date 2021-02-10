// Created: 11.01.2017
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

/**
 * {@link OutputStream} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringOutputStream extends OutputStream
{
    /**
    *
    */
    private long bytesWritten;

    /**
       *
       */
    private final LongConsumer bytesWrittenConsumer;

    /**
    *
    */
    private final boolean closeDelegate;

    /**
    *
    */
    private final OutputStream delegate;

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param bytesWrittenConsumer {@link BiConsumer}; Erster Parameter = Anzahl geschriebene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param closeDelegate boolean
     */
    public MonitoringOutputStream(final OutputStream delegate, final BiConsumer<Long, Long> bytesWrittenConsumer, final long size, final boolean closeDelegate)
    {
        this(delegate, bytesWritten -> bytesWrittenConsumer.accept(bytesWritten, size), closeDelegate);
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param bytesWrittenConsumer {@link LongConsumer}
     * @param closeDelegate boolean
     */
    public MonitoringOutputStream(final OutputStream delegate, final LongConsumer bytesWrittenConsumer, final boolean closeDelegate)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesWrittenConsumer = Objects.requireNonNull(bytesWrittenConsumer, "bytesWrittenConsumer required");
        this.closeDelegate = closeDelegate;
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        if (this.closeDelegate)
        {
            this.delegate.close();
        }
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

        this.bytesWritten += b.length;

        this.bytesWrittenConsumer.accept(this.bytesWritten);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        this.delegate.write(b, off, len);

        this.bytesWritten += len;

        this.bytesWrittenConsumer.accept(this.bytesWritten);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        this.delegate.write(b);

        this.bytesWritten++;

        this.bytesWrittenConsumer.accept(this.bytesWritten);
    }
}
