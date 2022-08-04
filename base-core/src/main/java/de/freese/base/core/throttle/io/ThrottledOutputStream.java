// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.freese.base.core.throttle.Throttler;

/**
 * @author Thomas Freese
 */
public class ThrottledOutputStream extends OutputStream
{
    /**
     *
     */
    private final OutputStream outputStream;
    /**
     *
     */
    private final Throttler throttler;
    /**
     *
     */
    private long bytesWrite;
    /**
     *
     */
    private long totalSleepTimeNanos;

    /**
     * Erstellt ein neues {@link ThrottledOutputStream} Object.
     *
     * @param outputStream {@link OutputStream}
     * @param throttler {@link Throttler}
     */
    public ThrottledOutputStream(final OutputStream outputStream, final Throttler throttler)
    {
        super();

        this.outputStream = Objects.requireNonNull(outputStream, "outputStream required");
        this.throttler = Objects.requireNonNull(throttler, "throttler required");
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.outputStream.close();
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        this.outputStream.flush();
    }

    /**
     * @return long
     */
    public long getTotalBytesWrite()
    {
        return this.bytesWrite;
    }

    /**
     * @return long
     */
    public long getTotalSleepTimeNanos()
    {
        return this.totalSleepTimeNanos;
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
        sb.append(", bytesWrite=").append(getTotalBytesWrite());
        sb.append(", totalSleepTimeMillis=").append(TimeUnit.NANOSECONDS.toMillis(getTotalSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        throttle(1);

        this.outputStream.write(b);
        this.bytesWrite++;
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
