/**
 * Created: 29.03.2020
 */

package de.freese.base.core.io.throttle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import de.freese.base.core.throttle.Throttle;

/**
 * @author Thomas Freese
 */
public class ThrottleInputStream extends InputStream
{
    /**
     *
     */
    private long bytesRead = 0;

    /**
     *
     */
    private final InputStream inputStream;

    /**
     *
     */
    private final Throttle throttle;

    /**
     *
     */
    private long totalSleepTimeNanos = 0;

    /**
     * Erstellt ein neues {@link ThrottleInputStream} Object.
     *
     * @param inputStream {@link InputStream}
     * @param throttle {@link Throttle}
     */
    public ThrottleInputStream(final InputStream inputStream, final Throttle throttle)
    {
        super();

        this.inputStream = Objects.requireNonNull(inputStream, "inputStream required");
        this.throttle = Objects.requireNonNull(throttle, "throttle required");
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
     * @return double
     */
    public double getBytesPerSec()
    {
        return this.throttle.getRate();
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
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        throttle(b.length);

        int readLen = this.inputStream.read(b);

        if (readLen != -1)
        {
            this.bytesRead += readLen;
        }

        return readLen;
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        throttle(len);

        int readLen = this.inputStream.read(b, off, len);

        if (readLen != -1)
        {
            this.bytesRead += readLen;
        }

        return readLen;
    }

    /**
     * @param permits int
     */
    protected void throttle(final int permits)
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
        sb.append(", bytesRead=").append(this.bytesRead);
        sb.append(", bytesPerSec=").append(getBytesPerSec());
        sb.append(", totalSleepTimeMillis=").append(TimeUnit.NANOSECONDS.toMillis(getTotalSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }
}
