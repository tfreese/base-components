// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.freese.base.core.throttle.Throttler;

/**
 * @author Thomas Freese
 */
public class ThrottledInputStream extends FilterInputStream {

    private final Throttler throttler;

    private long bytesRead;
    private long sleepTimeNanos;

    public ThrottledInputStream(final InputStream inputStream, final Throttler throttler) {
        super(inputStream);

        this.throttler = Objects.requireNonNull(throttler, "throttler required");
    }

    public long getBytesRead() {
        return this.bytesRead;
    }

    public long getSleepTimeNanos() {
        return this.sleepTimeNanos;
    }

    @Override
    public int read() throws IOException {
        throttle(1);

        final int data = super.read();

        if (data != -1) {
            this.bytesRead++;
        }

        return data;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        if (len == 0) {
            return 0;
        }

        int c = read();

        if (c == -1) {
            return -1;
        }

        b[off] = (byte) c;

        int i = 1;

        try {
            for (; i < len; i++) {
                c = read();

                if (c == -1) {
                    break;
                }

                b[off + i] = (byte) c;
            }
        }
        catch (IOException ex) {
            // Ignore
        }

        return i;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("throttle=").append(this.throttler);
        sb.append(", bytesRead=").append(this.bytesRead);
        sb.append(", sleepTimeNanos=").append(TimeUnit.NANOSECONDS.toMillis(getSleepTimeNanos()));
        sb.append("]");

        return sb.toString();
    }

    protected InputStream getDelegate() {
        return this.in;
    }

    private void throttle(final int permits) {
        final long waitNanos = this.throttler.reservePermits(permits);

        if (waitNanos > 0L) {
            try {
                TimeUnit.NANOSECONDS.sleep(waitNanos);
            }
            catch (InterruptedException ex) {
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }

            this.sleepTimeNanos += waitNanos;
        }
    }
}
