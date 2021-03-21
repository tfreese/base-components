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
     *
     */
    private final MessageDigest messageDigest;

    /**
     * Erstellt ein neues {@link AbstractResourceCache} Object.
     */
    protected AbstractResourceCache()
    {
        super();

        this.messageDigest = createMessageDigest();
    }

    /**
     * Erzeugt den MessageDigest für die Generierung des Keys.<br>
     * Beim Auftreten einer {@link NoSuchAlgorithmException} wird diese in eine {@link RuntimeException} konvertiert.
     *
     * @return {@link MessageDigest}
     */
    protected MessageDigest createMessageDigest()
    {
        MessageDigest md = null;

        try
        {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException ex)
        {
            try
            {
                md = MessageDigest.getInstance("MD5");
            }
            catch (final NoSuchAlgorithmException ex2)
            {
                throw new RuntimeException(ex2);
            }
        }

        return md;
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

        if ("file".equals(protocol))
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
    protected InputStream toInputStream(final URL url) throws Exception
    {
        URLConnection connection = url.openConnection();

        try
        {
            if (connection instanceof HttpURLConnection)
            {
                HttpURLConnection httpURLConnection = ((HttpURLConnection) connection);

                // Verhindert HTTP 301 Moved Permanently. -> funktioniert aber nicht !
                // httpURLConnection.setInstanceFollowRedirects(true);

                int status = httpURLConnection.getResponseCode();

                if ((status == HttpURLConnection.HTTP_MOVED_TEMP) || (status == HttpURLConnection.HTTP_MOVED_PERM)
                        || (status == HttpURLConnection.HTTP_SEE_OTHER))
                {
                    // get redirect url from "location" header field
                    String newUrl = httpURLConnection.getHeaderField("Location");

                    // get the cookie if need, for login
                    String cookies = httpURLConnection.getHeaderField("Set-Cookie");

                    httpURLConnection.disconnect();

                    httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                    httpURLConnection.setRequestProperty("Cookie", cookies);
                    // conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    // conn.addRequestProperty("User-Agent", "Mozilla");
                    // conn.addRequestProperty("Referer", "google.com");
                }

                return httpURLConnection.getInputStream();
            }

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
