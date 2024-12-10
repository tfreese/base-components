// Created: 11.01.2017
package de.freese.base.core.nio;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

/**
 * @author Thomas Freese
 */
public class MonitoringOutputStream extends FilterOutputStream {
    private final LongConsumer bytesWrittenConsumer;
    private final boolean closeDelegate;

    private long bytesWritten;

    /**
     * @param bytesWrittenConsumer {@link BiConsumer}; 1st Parameter = bytesRead, 2nd Parameter = Size
     */
    public MonitoringOutputStream(final OutputStream delegate, final BiConsumer<Long, Long> bytesWrittenConsumer, final long size, final boolean closeDelegate) {
        this(delegate, bw -> bytesWrittenConsumer.accept(bw, size), closeDelegate);
    }

    public MonitoringOutputStream(final OutputStream delegate, final LongConsumer bytesWrittenConsumer, final boolean closeDelegate) {
        super(delegate);

        this.bytesWrittenConsumer = Objects.requireNonNull(bytesWrittenConsumer, "bytesWrittenConsumer required");
        this.closeDelegate = closeDelegate;
    }

    @Override
    public void close() throws IOException {
        if (closeDelegate) {
            super.close();
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);

        bytesWritten += len;

        bytesWrittenConsumer.accept(bytesWritten);
    }

    @Override
    public void write(final int b) throws IOException {
        super.write(b);

        bytesWritten++;

        bytesWrittenConsumer.accept(bytesWritten);
    }
}
