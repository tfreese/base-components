package de.freese.base.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * {@link PrintWriter} mit fest codierten LineSeparator (println) für Windows.
 *
 * @author Thomas Freese
 */
public class WindowsPrintWriter extends PrintWriter {
    private static final String LINE_SEPARATOR = "\r\n";

    private boolean autoFlush;

    public WindowsPrintWriter(final File file, final Charset charset) throws IOException {
        super(file, charset);
    }

    public WindowsPrintWriter(final OutputStream out, final boolean autoFlush, final Charset charset) {
        super(out, autoFlush, charset);

        this.autoFlush = autoFlush;
    }

    public WindowsPrintWriter(final String fileName, final Charset charset) throws IOException {
        super(fileName, charset);
    }

    public WindowsPrintWriter(final Writer out, final boolean autoFlush) {
        super(out, autoFlush);

        this.autoFlush = autoFlush;
    }

    /**
     * Implementierung entspricht PrintWriter.newLine Methode.
     */
    @Override
    public void println() {
        synchronized (lock) {
            try {
                if (out == null) {
                    throw new IOException("Stream closed");
                }

                out.write(LINE_SEPARATOR);

                if (autoFlush) {
                    out.flush();
                }
            }
            catch (InterruptedIOException _) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
            catch (IOException _) {
                setError();
            }
        }
    }
}
