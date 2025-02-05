// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.throttle.Throttler;

/**
 * @author Thomas Freese
 */
public class ThrottledInputStream extends FilterInputStream {

    private final Throttler throttler;

    private long bytesRead;

    public ThrottledInputStream(final InputStream inputStream, final Throttler throttler) {
        super(inputStream);

        this.throttler = Objects.requireNonNull(throttler, "throttler required");
    }

    public long getBytesRead() {
        return bytesRead;
    }

    @Override
    public int read() throws IOException {
        throttle(1);

        final int data = super.read();

        if (data != -1) {
            bytesRead++;
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
        sb.append("throttle=").append(throttler);
        sb.append(", bytesRead=").append(bytesRead);
        sb.append("]");

        return sb.toString();
    }

    private void throttle(final int permits) {
        throttler.acquirePermits(permits);

        // final long waitNanos = throttler.reservePermits(permits);
        //
        // if (waitNanos > 0L) {
        //     try {
        //         TimeUnit.NANOSECONDS.sleep(waitNanos);
        //     }
        //     catch (InterruptedException ex) {
        //         // Preserve interrupt status
        //         Thread.currentThread().interrupt();
        //     }
        //
        //     sleepTimeNanos += waitNanos;
        // }
    }
}
