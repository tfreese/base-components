// Created: 29.03.2020
package de.freese.base.core.io.throttle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.freese.base.core.throttle.Throttle;

/**
 * @author Thomas Freese
 */
public class ThrottleOutputStream extends OutputStream
{
    /**
     *
     */
    private final OutputStream outputStream;
    /**
     *
     */
    private final Throttle throttle;
    /**
     *
     */
    private long bytesWrite;
    /**
     *
     */
    private long totalSleepTimeNanos;

    /**
     * Erstellt ein neues {@link ThrottleOutputStream} Object.
     *
     * @param outputStream {@link OutputStream}
     * @param throttle {@link Throttle}
     */
    public ThrottleOutputStream(final OutputStream outputStream, final Throttle throttle)
    {
        super();

        this.outputStream = Objects.requireNonNull(outputStream, "outputStream required");
        this.throttle = Objects.requireNonNull(throttle, "throttle required");
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
     * @return double
     */
    public double getBytesPerSec()
    {
        return this.throttle.getRate();
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
     * @param permits int
     *
     * @throws IOException Falls was schiefgeht.
     */
    public void throttle(final int permits) throws IOException
    {
        this.totalSleepTimeNanos += this.throttle.acquireUnchecked(permits);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("throttle=").append(this.throttle);
        sb.append(", bytesWrite=").append(getTotalBytesWrite());
        sb.append(", bytesPerSec=").append(getBytesPerSec());
        sb.append(", totalSleepTimeMillis=").append(TimeUnit.NANOSECONDS.toMillis(getTotalSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        throttle(len);

        this.outputStream.write(b, off, len);

        this.bytesWrite += len;
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
}
