package de.freese.base.net.protocol;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * Basisklasse fuer Netzwerkprotokolle.
 *
 * @author Thomas Freese
 */
public abstract class AbstractProtocol implements AutoCloseable
{
    /**
     *
     */
    private PrintStream debugStream;

    /**
     *
     */
    private boolean isDebug;

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @SuppressWarnings("resource")
    @Override
    public void close() throws Exception
    {
        if (getDebugStream() != null)
        {
            getDebugStream().close();
        }
    }

    /**
     * @param value int
     */
    @SuppressWarnings("resource")
    protected void debug(final int value)
    {
        if (!isDebug())
        {
            return;
        }

        getDebugStream().write(value);
    }

    /**
     * @param text String
     */
    @SuppressWarnings("resource")
    protected void debug(final String text)
    {
        if (!isDebug())
        {
            return;
        }

        getDebugStream().println(text);
    }

    /**
     * @return {@link PrintStream}
     */
    public PrintStream getDebugStream()
    {
        if (this.debugStream == null)
        {
            this.debugStream = System.err;
        }

        return this.debugStream;
    }

    /**
     * Gets the APOP message digest. From RFC 1939: The 'digest' parameter is calculated by applying the MD5 algorithm [RFC1321] to a string consisting of the
     * timestamp (including angle-brackets) followed by a shared secret. The 'digest' parameter itself is a 16-octet value which is sent in hexadecimal format,
     * using lower-case ASCII characters.
     *
     * @param password The APOP password
     * @return The APOP digest or an empty string if an error occurs.
     */
    protected String getDigest(final String password)
    {
        String key = password;
        byte[] digest;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException ex)
        {
            return null;
        }

        return Hex.encodeHexString(digest);
    }

    /**
     * @return boolean
     */
    public boolean isDebug()
    {
        return this.isDebug;
    }

    /**
     * @param isDebug boolean
     */
    public void setDebug(final boolean isDebug)
    {
        this.isDebug = isDebug;
    }

    /**
     * @param debugStream {@link PrintStream}
     */
    public void setDebugStream(final PrintStream debugStream)
    {
        this.debugStream = debugStream;
    }
}
