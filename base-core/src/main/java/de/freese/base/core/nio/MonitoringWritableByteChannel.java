// Created: 26.10.2016
package de.freese.base.core.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

/**
 * {@link WritableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringWritableByteChannel implements WritableByteChannel
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
    private final boolean closeDelegate;

    /**
     *
     */
    private final WritableByteChannel delegate;

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringWritableByteChannel}
     *
     * @param delegate {@link WritableByteChannel}
     * @param bytesWrittenConsumer {@link BiConsumer}; Erster Parameter = Anzahl geschriebene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param closeDelegate boolean
     */
    public MonitoringWritableByteChannel(final WritableByteChannel delegate, final BiConsumer<Long, Long> bytesWrittenConsumer, final long size,
            final boolean closeDelegate)
    {
        this(delegate, bytesWritten -> bytesWrittenConsumer.accept(bytesWritten, size), closeDelegate);
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringWritableByteChannel}
     *
     * @param delegate {@link WritableByteChannel}
     * @param bytesWrittenConsumer {@link LongConsumer}
     * @param closeDelegate boolean
     */
    public MonitoringWritableByteChannel(final WritableByteChannel delegate, final LongConsumer bytesWrittenConsumer, final boolean closeDelegate)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesWrittenConsumer = Objects.requireNonNull(bytesWrittenConsumer, "bytesWrittenConsumer required");
        this.closeDelegate = closeDelegate;
    }

    /**
     * @see java.nio.channels.Channel#close()
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
     * @see java.nio.channels.Channel#isOpen()
     */
    @Override
    public boolean isOpen()
    {
        return this.delegate.isOpen();
    }

    /**
     * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
     */
    @Override
    public int write(final ByteBuffer src) throws IOException
    {
        int writeCount = this.delegate.write(src);

        if (writeCount > 0)
        {
            this.bytesWritten += writeCount;

            this.bytesWrittenConsumer.accept(this.bytesWritten);
        }

        return writeCount;
    }
}
