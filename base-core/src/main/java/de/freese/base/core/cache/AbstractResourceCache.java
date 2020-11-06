/**
 * Created: 27.07.2016
 */
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * Basis-Implementierung eines {@link ResourceCache}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractResourceCache implements ResourceCache
{
    /**
     * Erzeugt den MessageDigest für die Generierung des Keys.<br>
     * Beim Auftreten einer {@link NoSuchAlgorithmException} wird diese in eine {@link RuntimeException} konvertiert.
     *
     * @return {@link MessageDigest}
     */
    protected static MessageDigest createMessageDigest()
    {
        MessageDigest messageDigest = null;

        try
        {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException ex)
        {
            try
            {
                messageDigest = MessageDigest.getInstance("MD5");
            }
            catch (final NoSuchAlgorithmException ex2)
            {
                throw new RuntimeException(ex2);
            }
        }

        return messageDigest;
    }

    /**
     *
     */
    private final MessageDigest messageDigest;

    /**
     * Erstellt ein neues {@link AbstractResourceCache} Object.
     */
    public AbstractResourceCache()
    {
        super();

        this.messageDigest = createMessageDigest();
    }

    /**
     * Erzeugt den Key auf dem Resource-Pfad.<br>
     * Die Bytes des MessageDigest werden dafür in einen Hex-String umgewandelt.
     *
     * @param url {@link URL}
     * @return String
     */
    protected String generateKey(final URL url)
    {
        String urlString = url.toString();
        byte[] digest = getMessageDigest().digest(urlString.getBytes(StandardCharsets.UTF_8));
        String hex = Hex.encodeHexString(digest, false);

        return hex;
    }

    /**
     * @param url {@link URL}
     * @return long
     * @throws IOException Falls was schief geht.
     */
    protected long getContentLength(final URL url) throws IOException
    {
        String protocol = url.getProtocol();

        if (protocol.equals("file"))
        {
            // Proceed with file system resolution
            Path path = null;

            try
            {
                path = Path.of(url.toURI());
            }
            catch (URISyntaxException ex)
            {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                path = Paths.get(url.getFile());
            }

            long length = Files.size(path);

            return length;
        }

        // Try a URL connection content-length header
        URLConnection con = url.openConnection();

        if (con instanceof HttpURLConnection)
        {
            ((HttpURLConnection) con).setRequestMethod("HEAD");
        }

        long length = con.getContentLengthLong();

        return length;
    }

    /**
     * @return {@link MessageDigest}
     */
    protected MessageDigest getMessageDigest()
    {
        return this.messageDigest;
    }

    /**
     * @param url {@link URL}
     * @return {@link InputStream}
     * @throws Exception Falls was schief geht.
     */
    protected InputStream loadInputStream(final URL url) throws Exception
    {
        URLConnection connection = url.openConnection();

        try
        {
            return connection.getInputStream();
        }
        catch (IOException ex)
        {
            if (connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection) connection).disconnect();
            }

            throw ex;
        }
    }
}
