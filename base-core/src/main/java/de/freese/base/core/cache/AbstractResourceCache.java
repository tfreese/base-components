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
 * @author Thomas Freese
 */
public abstract class AbstractResourceCache implements ResourceCache
{
    private final HexFormat hexFormat;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageDigest messageDigest;

    protected AbstractResourceCache()
    {
        super();

        this.messageDigest = createMessageDigest();
        this.hexFormat = HexFormat.of().withUpperCase();
    }

    public HexFormat getHexFormat()
    {
        return this.hexFormat;
    }

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

    protected long getContentLength(final URI uri) throws IOException
    {
        String protocol = uri.getScheme();

        if ("file".equals(protocol))
        {
            Path path = Path.of(uri);

            return Files.size(path);
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

    protected Logger getLogger()
    {
        return logger;
    }

    protected MessageDigest getMessageDigest()
    {
        return this.messageDigest;
    }

    protected InputStream toInputStream(final URI uri) throws Exception
    {
        URLConnection connection = uri.toURL().openConnection();

        try
        {
            if (connection instanceof HttpURLConnection httpURLConnection)
            {
                // Avoid HTTP 301 Moved Permanently. -> but does not work !
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
