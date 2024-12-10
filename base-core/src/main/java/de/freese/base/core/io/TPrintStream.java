package de.freese.base.core.io;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Schreibt die Daten in beide PrintStreams.
 *
 * @author Thomas Freese
 */
public class TPrintStream extends PrintStream {
    private final PrintStream out2;

    public TPrintStream(final PrintStream out1, final PrintStream out2, final boolean autoFlush1) {
        super(out1, autoFlush1, StandardCharsets.UTF_8);

        this.out2 = out2;
    }

    @Override
    public void close() {
        super.close();

        out2.close();
    }

    @Override
    public void flush() {
        super.flush();

        out2.flush();
    }

    @Override
    public void write(final byte[] buf, final int off, final int len) {
        super.write(buf, off, len);

        out2.write(buf, off, len);
    }
}
