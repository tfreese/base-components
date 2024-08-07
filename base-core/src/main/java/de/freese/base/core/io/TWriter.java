package de.freese.base.core.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Schreibt die Daten in beide Writer.
 *
 * @author Thomas Freese
 */
public class TWriter extends Writer {
    private final Writer out1;
    private final Writer out2;

    public TWriter(final Writer out1, final Writer out2) {
        super();

        this.out1 = out1;
        this.out2 = out2;
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;

        try {
            this.out1.close();
        }
        catch (IOException ex) {
            exception = ex;
        }

        try {
            this.out2.close();
        }
        catch (IOException ex) {
            if (exception != null) {
                throw exception;
            }

            throw ex;
        }
    }

    @Override
    public void flush() throws IOException {
        this.out1.flush();
        this.out2.flush();
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        this.out1.write(cbuf, off, len);
        this.out2.write(cbuf, off, len);
    }
}
