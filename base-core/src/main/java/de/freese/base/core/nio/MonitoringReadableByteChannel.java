// Created: 11.01.2017
package de.freese.base.core.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

/**
 * {@link ReadableByteChannel} mit der Möglichkeit zur Überwachung durch einen Monitor.<br>
 *
 * @author Thomas Freese
 */
public class MonitoringReadableByteChannel implements ReadableByteChannel
{
    /**
     *
     */
    private long bytesRead;

    /**
    *
    */
    private final LongConsumer bytesReadConsumer;

    /**
    *
    */
    private final boolean closeDelegate;

    /**
    *
    */
    private final ReadableByteChannel delegate;

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringReadableByteChannel}
     *
     * @param delegate {@link ReadableByteChannel}
     * @param bytesReadConsumer {@link BiConsumer}; Erster Parameter = Anzahl gelesene Bytes, zweiter Parameter = Gesamtgröße
     * @param size long; Anzahl Bytes (Größe) des gesamten Channels
     * @param closeDelegate boolean
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final BiConsumer<Long, Long> bytesReadConsumer, final long size,
            final boolean closeDelegate)
    {
        this(delegate, bytesRead -> bytesReadConsumer.accept(bytesRead, size), closeDelegate);
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringReadableByteChannel}
     *
     * @param delegate {@link ReadableByteChannel}
     * @param bytesReadConsumer {@link LongConsumer}
     * @param closeDelegate boolean
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final LongConsumer bytesReadConsumer, final boolean closeDelegate)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesReadConsumer = Objects.requireNonNull(bytesReadConsumer, "bytesReadConsumer required");
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
     * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException
    {
        int readCount = this.delegate.read(dst);

        if (readCount > 0)
        {
            this.bytesRead += readCount;

            this.bytesReadConsumer.accept(this.bytesRead);
        }

        return readCount;
    }
}
