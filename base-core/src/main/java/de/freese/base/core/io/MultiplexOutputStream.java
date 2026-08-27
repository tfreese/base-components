package de.freese.base.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 25.10.2024
 */
public final class MultiplexOutputStream extends OutputStream {
    private final List<OutputStream> outputStreams;

    public MultiplexOutputStream(final List<OutputStream> outputStreams) {
        super();

        this.outputStreams = Objects.requireNonNull(outputStreams, "outputStreams required");
    }

    @Override
    public void close() throws IOException {
        for (final OutputStream outputStream : outputStreams) {
            outputStream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        for (final OutputStream outputStream : outputStreams) {
            outputStream.flush();
        }
    }

    @Override
    public void write(final int b) throws IOException {
        for (final OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void write(final byte @NonNull [] b) throws IOException {
        for (final OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void write(final byte @NonNull [] b, final int off, final int len) throws IOException {
        for (final OutputStream outputStream : outputStreams) {
            outputStream.write(b, off, len);
        }
    }
}
