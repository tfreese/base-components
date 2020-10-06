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
public class MonitorOutputStream extends OutputStream
{
    /**
    *
    */
    private long bytesWritten = 0;

    /**
       *
       */
    private final LongConsumer bytesWrittenConsumer;

    /**
    *
    */
    private final OutputStream delegate;

    /**
     * Erzeugt eine neue Instanz von {@link MonitorOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param bytesWrittenConsumer {@link BiConsumer}; Erster Parameter = Anzahl geschriebene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     */
    public MonitorOutputStream(final OutputStream delegate, final BiConsumer<Long, Long> bytesWrittenConsumer, final long size)
    {
        this(delegate, bytesWritten -> bytesWrittenConsumer.accept(bytesWritten, size));
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitorOutputStream}
     *
     * @param delegate {@link OutputStream}
     * @param bytesWrittenConsumer {@link LongConsumer}
     */
    public MonitorOutputStream(final OutputStream delegate, final LongConsumer bytesWrittenConsumer)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesWrittenConsumer = Objects.requireNonNull(bytesWrittenConsumer, "bytesWrittenConsumer required");
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
