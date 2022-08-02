// Created: 27.07.2016
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final HexFormat hexFormat;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
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
        this.hexFormat = HexFormat.of().withUpperCase();
    }

    /**
     * @return HexFormat
     */
    public HexFormat getHexFormat()
    {
        return this.hexFormat;
    }

    /**
     * Erzeugt den MessageDigest für die Generierung des Keys.<br>
     * Beim Auftreten einer {@link NoSuchAlgorithmException} wird diese in eine {@link RuntimeException} konvertiert.
     *
     * @return {@link MessageDigest}
     */
    protected MessageDigest createMessageDigest()
    {
        // String algorithm ="SHA"; // 40 Zeichen
        // String algorithm ="SHA-1"; // 40 Zeichen
        // String algorithm ="SHA-256"; // 64 Zeichen
        // String algorithm ="SHA-384"; // 96 Zeichen
        String algorithm = "SHA-512"; // 128 Zeichen

        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException ex)
        {
            getLogger().error("Algorithm '{}' not found, trying 'MD5'", algorithm);

            try
            {
                return MessageDigest.getInstance("MD5"); // 32 Zeichen
            }
            catch (final NoSuchAlgorithmException ex2)
            {
                throw new RuntimeException(ex2);
            }
        }
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
        String uriString = uri.toString();
        //        byte[] uriBytes = uriString.getBytes(StandardCharsets.UTF_8);
        //        byte[] digest = getMessageDigest().digest(uriBytes);
        //
        //        return getHexFormat().formatHex(digest);

        uriString = uriString.replace(':', '/');
        uriString = uriString.replace('?', '/');
        uriString = uriString.replace('&', '/');
        uriString = uriString.replace(' ', '_');
        uriString = uriString.replace("%20", "_");

        while (uriString.contains("//"))
        {
            uriString = uriString.replace("//", "/");
        }

        return uriString;
    }

    /**
     * @param uri {@link URI}
     *
     * @return long
     *
     * @throws IOException Falls was schiefgeht.
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
            URLConnection connection = uri.toURL().openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("HEAD");

            boolean redirect = false;
            int status = httpURLConnection.getResponseCode();

            if ((status == HttpURLConnection.HTTP_MOVED_TEMP) || (status == HttpURLConnection.HTTP_MOVED_PERM) || (status == HttpURLConnection.HTTP_SEE_OTHER))
            {
                redirect = true;
            }

            if (redirect)
            {
                // get redirect url from "location" header field
                String newUrl = httpURLConnection.getHeaderField("Location");

                // pen the new connection again
                httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                httpURLConnection.setRequestMethod("HEAD");
            }

            return httpURLConnection.getContentLengthLong();
        }

        throw new IOException("unsupported protocol");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return logger;
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
     * @throws Exception Falls was schiefgeht.
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

                    // get the cookie if we need, for login
                    String cookies = httpURLConnection.getHeaderField("Set-Cookie");

                    httpURLConnection.disconnect();

                    httpURLConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                    httpURLConnection.setRequestProperty("Cookie", cookies);
                }

                return httpURLConnection.getInputStream();
            }

            return connection.getInputStream();
        }
        catch (IOException ex)
        {
            if (connection instanceof HttpURLConnection httpURLConnection)
            {
                httpURLConnection.disconnect();
            }

            throw ex;
        }
    }
}
