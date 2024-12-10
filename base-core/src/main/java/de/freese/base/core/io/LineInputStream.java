package de.freese.base.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * This class is to support reading CRLF terminated lines that contain only US-ASCII characters from an input stream. Provides functionality that is similar to
 * the deprecated <code>DataInputStream.readLine()</code>. Expected use is to read lines as String objects from a RFC822 stream. It is implemented as a
 * FilterInputStream, so one can just wrap this class around any input stream and read bytes from this filter.
 *
 * @author John Mani
 */
public final class LineInputStream extends FilterInputStream {
    /**
     * reusable byte buffer
     */
    private char[] lineBuffer;

    public LineInputStream(final InputStream in) {
        super(in);
    }

    /**
     * Read a line containing only ASCII characters from the input stream. A line is terminated by a CR or NL or CR-NL sequence. A common error is a CR-CR-NL
     * sequence, which will also terminate a line. The line terminator is not returned as part of the returned String. Returns null if no data is available.
     * <p>
     * This class is similar to the deprecated <code>DataInputStream.readLine()</code>
     * </p>
     */
    public String readLine() throws IOException {
        if (lineBuffer == null) {
            lineBuffer = new char[128];
        }

        int c1;
        int room = lineBuffer.length;
        int offset = 0;

        while ((c1 = in.read()) != -1) {
            if (c1 == '\n') // Got NL
            {
                break;
            }
            else if (c1 == '\r') {
                // Got CR, is the next char NL ?
                int c2 = in.read();

                if (c2 == '\r') // discard extraneous CR
                {
                    c2 = in.read();
                }

                if (c2 != '\n') {
                    // If not NL, push it back
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }

                    ((PushbackInputStream) in).unread(c2);
                }

                break;
            }

            // Not CR, NL or CR-NL.
            // Insert the byte into our byte buffer.
            if (--room < 0) {
                // No room, need to grow.
                lineBuffer = new char[offset + 128];
                room = lineBuffer.length - offset - 1;
                System.arraycopy(lineBuffer, 0, lineBuffer, 0, offset);
            }

            lineBuffer[offset++] = (char) c1;
        }

        if (c1 == -1 && offset == 0) {
            return null;
        }

        return String.copyValueOf(lineBuffer, 0, offset);
    }
}
