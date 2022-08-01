// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class SleepThrottledInputStream extends InputStream
{
    /**
     *
     */
    private static final double ONE_SECOND_NANOS = 1_000_000_000.0D;
    /**
     *
     */
    private static final long SLEEP_DURATION_MS = 10;
    /**
     *
     */
    private final InputStream inputStream;
    /**
     *
     */
    private final long maxBytesPerSec;
    /**
     *
     */
    private final long startTime = System.nanoTime();
    /**
     *
     */
    private long bytesRead;
    /**
     *
     */
    private long totalSleepTimeMillis;

    /**
     * Erstellt ein neues {@link SleepThrottledInputStream} Object.
     *
     * @param inputStream {@link InputStream}
     */
    public SleepThrottledInputStream(final InputStream inputStream)
    {
        this(inputStream, Long.MAX_VALUE);
    }

    /**
     * Erstellt ein neues {@link SleepThrottledInputStream} Object.
     *
     * @param inputStream {@link InputStream}
     * @param maxBytesPerSec long
     */
    public SleepThrottledInputStream(final InputStream inputStream, final long maxBytesPerSec)
    {
        super();

        this.inputStream = Objects.requireNonNull(inputStream, "inputStream required");

        if (maxBytesPerSec < 0)
        {
            throw new IllegalArgumentException("maxBytesPerSec should be greater than zero");
        }

        this.maxBytesPerSec = maxBytesPerSec;
    }

    /**
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException
    {
        return this.inputStream.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.inputStream.close();
    }

    /**
     * @return long
     */
    public long getBytesPerSec()
    {
        double elapsed = (System.nanoTime() - this.startTime) / ONE_SECOND_NANOS;

        if (elapsed == 0.0D)
        {
            return this.bytesRead;
        }

        return (long) (this.bytesRead / elapsed);
    }

    /**
     * @return long
     */
    public long getTotalBytesRead()
    {
        return this.bytesRead;
    }

    /**
     * @return long
     */
    public long getTotalSleepTimeMillis()
    {
        return this.totalSleepTimeMillis;
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        throttle();

        int data = this.inputStream.read();

        if (data != -1)
        {
            this.bytesRead++;
        }

        return data;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("bytesRead=").append(getTotalBytesRead());
        sb.append(", maxBytesPerSec=").append(this.maxBytesPerSec);
        sb.append(", bytesPerSec=").append(getBytesPerSec());
        sb.append(", totalSleepTimeMillis=").append(getTotalSleepTimeMillis());
        sb.append("]");

        return sb.toString();
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    protected void throttle() throws IOException
    {
        while (getBytesPerSec() > this.maxBytesPerSec)
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(SLEEP_DURATION_MS);
                this.totalSleepTimeMillis += SLEEP_DURATION_MS;
            }
            catch (InterruptedException ex)
            {
                throw new IOException("Thread interrupted", ex);
            }
        }
    }
}
