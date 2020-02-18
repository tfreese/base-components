package de.freese.base.core.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.freese.base.core.ByteUtils;

/**
 * This class is to support writing out Strings as a sequence of bytes terminated by a CRLF sequence. The String must contain only US-ASCII
 * characters.
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
    private static byte[] newline = null;

    static
    {
        newline = new byte[2];
        newline[0] = (byte) '\r';
        newline[1] = (byte) '\n';
    }

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
     * @throws IOException Falls was schief geht.
     */
    public void writeln() throws IOException
    {
        this.out.write(newline);
    }

    /**
     * @param s String
     * @throws IOException Falls was schief geht.
     */
    public void writeln(final String s) throws IOException
    {
        byte[] bytes = ByteUtils.toByteArray(s);
        this.out.write(bytes);
        this.out.write(newline);
    }
}
