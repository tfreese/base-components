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
        synchronized (this.lock) {
            try {
                if (this.out == null) {
                    throw new IOException("Stream closed");
                }

                this.out.write(LINE_SEPARATOR);

                if (this.autoFlush) {
                    this.out.flush();
                }
            }
            catch (InterruptedIOException ex) {
                Thread.currentThread().interrupt();
            }
            catch (IOException ex) {
                setError();
            }
        }
    }
}
