package de.freese.base.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * OutputStream für eine {@link CharSequence}.
 *
 * @author Thomas Freese
 */
public class StringOutputStream extends ByteArrayOutputStream
{
    /**
     * @param value {@link CharSequence}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public void write(final CharSequence value) throws IOException
    {
        super.write(value.toString().getBytes(StandardCharsets.UTF_8));
    }
}
