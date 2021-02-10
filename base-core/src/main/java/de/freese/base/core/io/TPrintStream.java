package de.freese.base.core.io;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Schreibt die Daten in beide PrintStreams.
 *
 * @author Thomas Freese
 */
public class TPrintStream extends PrintStream
{
    /**
     *
     */
    private final PrintStream out2;

    /**
     * Creates a new {@link TPrintStream} object.
     *
     * @param out1 {@link PrintStream}
     * @param out2 {@link PrintStream}
     * @param autoFlush1 boolean
     */
    public TPrintStream(final PrintStream out1, final PrintStream out2, final boolean autoFlush1)
    {
        super(out1, autoFlush1, StandardCharsets.UTF_8);

        this.out2 = out2;
    }

    /**
     * @see java.io.PrintStream#close()
     */
    @Override
    public void close()
    {
        super.close();

        this.out2.close();
    }

    /**
     * @see java.io.PrintStream#flush()
     */
    @Override
    public void flush()
    {
        super.flush();

        this.out2.flush();
    }

    /**
     * @see java.io.PrintStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] buf, final int off, final int len)
    {
        super.write(buf, off, len);

        this.out2.write(buf, off, len);
    }
}
