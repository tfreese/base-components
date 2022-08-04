// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class SleepThrottledOutputStream extends OutputStream
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
    private final long maxBytesPerSec;
    /**
     *
     */
    private final OutputStream outputStream;
    /**
     *
     */
    private final long startTime = System.nanoTime();
    /**
     *
     */
    private long bytesWrite;
    /**
     *
     */
    private long totalSleepTimeMillis;

    /**
     * Erstellt ein neues {@link SleepThrottledOutputStream} Object.
     *
     * @param outputStream {@link OutputStream}
     * @param maxBytesPerSec long
     */
    public SleepThrottledOutputStream(final OutputStream outputStream, final long maxBytesPerSec)
    {
        super();

        this.outputStream = Objects.requireNonNull(outputStream, "outputStream required");

        if (maxBytesPerSec <= 0)
        {
            throw new IllegalArgumentException("maxBytesPerSec should be greater than zero");
        }

        this.maxBytesPerSec = maxBytesPerSec;
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
    public long getBytesPerSec()
    {
        double elapsed = (System.nanoTime() - this.startTime) / ONE_SECOND_NANOS;

        if (elapsed == 0.0D)
        {
            return this.bytesWrite;
        }

        return (long) (this.bytesWrite / elapsed);
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
    public long getTotalSleepTimeMillis()
    {
        return this.totalSleepTimeMillis;
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    public void throttle() throws IOException
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
                System.out.println("Thread interrupted" + ex.getMessage());
                throw new IOException("Thread interrupted", ex);
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("bytesWrite=").append(getTotalBytesWrite());
        sb.append(", maxBytesPerSec=").append(this.maxBytesPerSec);
        sb.append(", bytesPerSec=").append(getBytesPerSec());
        sb.append(", totalSleepTimeMillis=").append(getTotalSleepTimeMillis());
        sb.append("]");

        return sb.toString();
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        if (len < this.maxBytesPerSec)
        {
            throttle();

            this.bytesWrite = this.bytesWrite + len;
            this.outputStream.write(b, off, len);

            return;
        }

        long currentOffSet = off;
        long remainingBytesToWrite = len;

        do
        {
            throttle();

            remainingBytesToWrite -= this.maxBytesPerSec;
            this.bytesWrite += this.maxBytesPerSec;
            this.outputStream.write(b, (int) currentOffSet, (int) this.maxBytesPerSec);
            currentOffSet = currentOffSet + this.maxBytesPerSec;

        }
        while (remainingBytesToWrite > this.maxBytesPerSec);

        throttle();

        this.bytesWrite += remainingBytesToWrite;
        this.outputStream.write(b, (int) currentOffSet, (int) remainingBytesToWrite);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        throttle();

        this.outputStream.write(b);
        this.bytesWrite++;
    }
}
