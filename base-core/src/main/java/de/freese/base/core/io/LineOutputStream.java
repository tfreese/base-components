package de.freese.base.core.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.freese.base.utils.ByteUtils;

/**
 * This class is to support writing out Strings as a sequence of bytes terminated by a CRLF sequence. The String must contain only US-ASCII characters.
 * <p>
 * The expected use is to write out RFC822 style headers to an output stream.
 * </p>
 *
 * @author John Mani
 */
public class LineOutputStream extends FilterOutputStream
{
    /**
     *
     */
    private static final byte[] NEW_LINE = new byte[]
            {
                    (byte) '\r', (byte) '\n'
            };

    /**
     * Creates a new {@link LineOutputStream} object.
     *
     * @param out {@link OutputStream}
     */
    public LineOutputStream(final OutputStream out)
    {
        super(out);
    }

    /**
     * @throws IOException Falls was schiefgeht.
     */
    public void writeln() throws IOException
    {
        this.out.write(NEW_LINE);
    }

    /**
     * @param s String
     *
     * @throws IOException Falls was schiefgeht.
     */
    public void writeln(final String s) throws IOException
    {
        byte[] bytes = ByteUtils.toBytes(s);
        this.out.write(bytes);
        this.out.write(NEW_LINE);
    }
}
