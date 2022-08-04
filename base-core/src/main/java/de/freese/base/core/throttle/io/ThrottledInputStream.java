// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.freese.base.core.throttle.Throttler;

/**
 * @author Thomas Freese
 */
public class ThrottledInputStream extends InputStream
{
    /**
     *
     */
    private final InputStream inputStream;
    /**
     *
     */
    private final Throttler throttler;
    /**
     *
     */
    private long bytesRead;
    /**
     *
     */
    private long totalSleepTimeNanos;

    /**
     * Erstellt ein neues {@link ThrottledInputStream} Object.
     *
     * @param inputStream {@link InputStream}
     * @param throttler {@link Throttler}
     */
    public ThrottledInputStream(final InputStream inputStream, final Throttler throttler)
    {
        super();

        this.inputStream = Objects.requireNonNull(inputStream, "inputStream required");
        this.throttler = Objects.requireNonNull(throttler, "throttler required");
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
    public long getTotalBytesRead()
    {
        return this.bytesRead;
    }

    /**
     * @return long
     */
    public long getTotalSleepTimeNanos()
    {
        return this.totalSleepTimeNanos;
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        throttle(1);

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
        sb.append("throttle=").append(this.throttler);
        sb.append(", bytesRead=").append(this.bytesRead);
        sb.append(", totalSleepTimeMillis=").append(TimeUnit.NANOSECONDS.toMillis(getTotalSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }

    /**
     * @param permits int
     *
     * @throws IOException Falls was schiefgeht.
     */
    private void throttle(final int permits) throws IOException
    {
        long waitNanos = this.throttler.reservePermits(permits);

        if (waitNanos > 0L)
        {
            try
            {
                TimeUnit.NANOSECONDS.sleep(waitNanos);
            }
            catch (InterruptedException ex)
            {
                throw new IOException(ex);
            }

            this.totalSleepTimeNanos += waitNanos;
        }
    }
}
