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
    private final InputStream inputStream;

    private final Throttler throttler;

    private long bytesRead;

    private long sleepTimeNanos;

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

    public long getBytesRead()
    {
        return this.bytesRead;
    }

    public long getSleepTimeNanos()
    {
        return this.sleepTimeNanos;
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
        sb.append(", sleepTimeNanos=").append(TimeUnit.NANOSECONDS.toMillis(getSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }

    private void throttle(final int permits)
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
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }

            this.sleepTimeNanos += waitNanos;
        }
    }
}
