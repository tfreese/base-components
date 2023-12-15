// Created: 27.07.2016
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
public abstract class AbstractResourceCache implements ResourceCache {
    private final HexFormat hexFormat;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MessageDigest messageDigest;

    protected AbstractResourceCache() {
        super();

        this.messageDigest = createMessageDigest();
        this.hexFormat = HexFormat.of().withUpperCase();
    }

    protected MessageDigest createMessageDigest() {
        //final String algorithm ="SHA"; // 40 Zeichen
        //final String algorithm ="SHA-1"; // 40 Zeichen
        //final String algorithm ="SHA-256"; // 64 Zeichen
        //final String algorithm ="SHA-384"; // 96 Zeichen
        final String algorithm = "SHA-512"; // 128 Zeichen

        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException ex) {
            getLogger().error("Algorithm '{}' not found, trying 'MD5'", algorithm);

            try {
                return MessageDigest.getInstance("MD5"); // 32 Zeichen
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    protected String generateKey(final URI uri) {
        final String uriString = uri.toString();
        final byte[] uriBytes = uriString.getBytes(StandardCharsets.UTF_8);
        final byte[] digest = getMessageDigest().digest(uriBytes);

        return getHexFormat().formatHex(digest);

        //        uriString = uriString.replace(':', '/');
        //        uriString = uriString.replace('?', '/');
        //        uriString = uriString.replace('&', '/');
        //        uriString = uriString.replace(' ', '_');
        //        uriString = uriString.replace("%20", "_");
        //
        //        while (uriString.contains("//"))
        //        {
        //            uriString = uriString.replace("//", "/");
        //        }
        //
        //        return uriString;
    }

    protected long getContentLength(final URI uri) throws IOException {
        final String protocol = uri.getScheme();

        if ("file".equals(protocol)) {
            final Path path = Path.of(uri);

            return Files.size(path);
        }
        else if ("http".equals(protocol) || "https".equals(protocol)) {
            final URLConnection connection = uri.toURL().openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("HEAD");

            boolean redirect = false;
            final int status = httpURLConnection.getResponseCode();

            if ((status == HttpURLConnection.HTTP_MOVED_TEMP) || (status == HttpURLConnection.HTTP_MOVED_PERM) || (status == HttpURLConnection.HTTP_SEE_OTHER)) {
                redirect = true;
            }

            if (redirect) {
                // get redirect url from "location" header field
                final String newUrl = httpURLConnection.getHeaderField("Location");

                // pen the new connection again
                httpURLConnection = (HttpURLConnection) URI.create(newUrl).toURL().openConnection();
                httpURLConnection.setRequestMethod("HEAD");
            }

            return httpURLConnection.getContentLengthLong();
        }

        throw new IOException("unsupported protocol");
    }

    protected HexFormat getHexFormat() {
        return this.hexFormat;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected MessageDigest getMessageDigest() {
        return this.messageDigest;
    }

    protected InputStream toInputStream(final URI uri) throws Exception {
        final URLConnection connection = uri.toURL().openConnection();

        try {
            if (connection instanceof HttpURLConnection httpURLConnection) {
                // To avoid 'HTTP 301 Moved Permanently' -> but does not work !
                // httpURLConnection.setInstanceFollowRedirects(true);

                final int status = httpURLConnection.getResponseCode();

                if ((status == HttpURLConnection.HTTP_MOVED_TEMP) || (status == HttpURLConnection.HTTP_MOVED_PERM) || (status == HttpURLConnection.HTTP_SEE_OTHER)) {
                    // get redirect url from "location" header field
                    final String newUrl = httpURLConnection.getHeaderField("Location");

                    // get the cookie if we need, for login
                    final String cookies = httpURLConnection.getHeaderField("Set-Cookie");

                    httpURLConnection.disconnect();

                    httpURLConnection = (HttpURLConnection) URI.create(newUrl).toURL().openConnection();
                    httpURLConnection.setRequestProperty("Cookie", cookies);
                }

                return httpURLConnection.getInputStream();
            }

            return connection.getInputStream();
        }
        catch (IOException ex) {
            if (connection instanceof HttpURLConnection httpURLConnection) {
                httpURLConnection.disconnect();
            }

            throw ex;
        }
    }
}
