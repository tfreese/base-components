// Created: 29.03.2020
package de.freese.base.core.throttle.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import de.freese.base.core.throttle.Throttler;

/**
 * @author Thomas Freese
 */
public class ThrottledOutputStream extends FilterOutputStream {

    private final Throttler throttler;

    private long bytesWritten;

    public ThrottledOutputStream(final OutputStream outputStream, final Throttler throttler) {
        super(outputStream);

        this.throttler = Objects.requireNonNull(throttler, "throttler required");
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("throttle=").append(throttler);
        sb.append(", bytesWritten=").append(getBytesWritten());
        sb.append("]");

        return sb.toString();
    }

    @Override
    public void write(final int b) throws IOException {
        throttle(1);

        super.write(b);

        bytesWritten++;
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
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
