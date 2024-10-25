// Created: 25 Okt. 2024
package de.freese.base.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class MultiplexOutputStream extends OutputStream {
    private final List<OutputStream> outputStreams;

    public MultiplexOutputStream(final List<OutputStream> outputStreams) {
        super();

        this.outputStreams = Objects.requireNonNull(outputStreams, "outputStreams required");
    }

    @Override
    public void close() throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.flush();
        }
    }

    @Override
    public void write(final int b) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void write(final byte[] b) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b, off, len);
        }
    }
}
