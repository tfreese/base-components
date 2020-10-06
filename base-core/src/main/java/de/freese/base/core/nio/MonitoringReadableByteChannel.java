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
    private long bytesRead = 0;

    /**
    *
    */
    private final LongConsumer bytesReadConsumer;

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
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final BiConsumer<Long, Long> bytesReadConsumer, final long size)
    {
        this(delegate, bytesRead -> bytesReadConsumer.accept(bytesRead, size));
    }

    /**
     * Erzeugt eine neue Instanz von {@link MonitoringReadableByteChannel}
     *
     * @param delegate {@link ReadableByteChannel}
     * @param bytesReadConsumer {@link LongConsumer}
     */
    public MonitoringReadableByteChannel(final ReadableByteChannel delegate, final LongConsumer bytesReadConsumer)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.bytesReadConsumer = Objects.requireNonNull(bytesReadConsumer, "bytesReadConsumer required");
    }

    /**
     * @see java.nio.channels.Channel#close()
     */
    @Override
    public void close() throws IOException
    {
        this.delegate.close();
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
