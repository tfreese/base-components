/**
 * Created: 27.07.2016
 */
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.freese.base.utils.ByteUtils;

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
     * @param uri {@link URI}
     *
     * @return String
     */
    protected String generateKey(final URI uri)
    {
        String urlString = uri.toString();
        byte[] digest = getMessageDigest().digest(urlString.getBytes(StandardCharsets.UTF_8));
        String hex = ByteUtils.bytesToHex(digest);

        return hex;
    }

    /**
     * @param uri {@link URI}
     *
     * @return long
     *
     * @throws IOException Falls was schief geht.
     */
    protected long getContentLength(final URI uri) throws IOException
    {
        String protocol = uri.getScheme();

        if ("file".equals(protocol))
        {
            Path path = Path.of(uri);

            long length = Files.size(path);

            return length;
        }
        else if ("http".equals(protocol) || "https".equals(protocol))
        {
            URLConnection con = uri.toURL().openConnection();

            if (con instanceof HttpURLConnection)
            {
                ((HttpURLConnection) con).setRequestMethod("HEAD");
            }

            long length = con.getContentLengthLong();

            return length;
        }

        throw new IOException("unsupported protocol");
    }

    /**
     * @return {@link MessageDigest}
     */
    protected MessageDigest getMessageDigest()
    {
        return this.messageDigest;
    }

    /**
     * @param uri {@link URI}
     *
     * @return {@link InputStream}
     *
     * @throws Exception Falls was schief geht.
     */
    protected InputStream toInputStream(final URI uri) throws Exception
    {
        URLConnection connection = uri.toURL().openConnection();

        try
        {
            if (connection instanceof HttpURLConnection httpURLConnection)
            {
                

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
