package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.activation.DataSource;

/**
 * Einfache {@link DataSource} für ein ByteArray.
 *
 * @author Thomas Freese
 */
public class ByteArrayDataSource implements DataSource, Serializable
{
    /**
     *
     */
    public static final String MIMETYPE_APPLICATION_EXCEL = "application/vnd.ms-excel";
    /**
     *
     */
    public static final String MIMETYPE_APPLICATION_HTTP = "application/http";
    /**
     * MimeTypes siehe auch <a href="http://www.iana.org/assignments/media-types">http://www.iana.org/assignments/media-types</a>
     */
    public static final String MIMETYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     *
     */
    public static final String MIMETYPE_APPLICATION_PDF = "application/pdf";
    /**
     *
     */
    public static final String MIMETYPE_APPLICATION_POWERPOINT = "application/vnd.ms-powerpoint";
    /**
     *
     */
    public static final String MIMETYPE_APPLICATION_XML = "application/xml";
    /**
     *
     */
    public static final String MIMETYPE_IMAGE_BMP = "image/bmp";
    /**
     *
     */
    public static final String MIMETYPE_IMAGE_GIF = "image/gif";
    /**
     *
     */
    public static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
    /**
     *
     */
    public static final String MIMETYPE_IMAGE_PNG = "image/png";
    /**
     *
     */
    public static final String MIMETYPE_TEXT_CSV = "text/csv";
    /**
     *
     */
    public static final String MIMETYPE_TEXT_HTML = "text/html";
    /**
     *
     */
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3420529375053580438L;

    /**
     * Liefert anhand des Resource Namens den passenden MimeType.
     *
     * @param resourceName String
     *
     * @return String
     */
    public static String getMimeType(final String resourceName)
    {
        if (resourceName == null)
        {
            return null;
        }

        String name = resourceName.toLowerCase();
        String mimeType = MIMETYPE_APPLICATION_OCTET_STREAM;

        if (name.startsWith("http"))
        {
            mimeType = MIMETYPE_APPLICATION_HTTP;
        }
        else if (name.endsWith(".pdf"))
        {
            mimeType = MIMETYPE_APPLICATION_PDF;
        }
        else if (name.endsWith(".xls"))
        {
            mimeType = MIMETYPE_APPLICATION_EXCEL;
        }
        else if (name.endsWith(".ppt"))
        {
            mimeType = MIMETYPE_APPLICATION_POWERPOINT;
        }
        else if (name.endsWith(".png"))
        {
            mimeType = MIMETYPE_IMAGE_PNG;
        }
        else if (name.endsWith(".gif"))
        {
            mimeType = MIMETYPE_IMAGE_GIF;
        }
        else if (name.endsWith(".bmp"))
        {
            mimeType = MIMETYPE_IMAGE_BMP;
        }
        else if (name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".jpe")
                || name.endsWith(".jfif") || name.endsWith(".pjpeg") || name.endsWith(".pjp"))
        {
            mimeType = MIMETYPE_IMAGE_JPEG;
        }

        return mimeType;
    }

    /**
     * Liefert true, wenn der MimeType ein ImageTyp ist.
     *
     * @param mimeType String
     *
     * @return boolean
     */
    public static boolean isImageMimeType(final String mimeType)
    {
        return MIMETYPE_IMAGE_JPEG.equals(mimeType) || MIMETYPE_IMAGE_GIF.equals(mimeType) || MIMETYPE_IMAGE_PNG.equals(mimeType)
                || MIMETYPE_IMAGE_BMP.equals(mimeType);
    }

    /**
     * Daten
     */
    private final byte[] data;
    /**
     * content-type
     */
    private String mimeType = MIMETYPE_APPLICATION_OCTET_STREAM;
    /**
     *
     */
    private String name = "";

    /**
     * Create a {@link ByteArrayDataSource} from a byte array.
     *
     * @param data byte[]
     * @param mimeType String
     */
    public ByteArrayDataSource(final byte[] data, final String mimeType)
    {
        super();

        this.data = Objects.requireNonNull(data, "data required");
        this.mimeType = Objects.requireNonNull(mimeType, "mimeType required");
    }

    /**
     * InputStream wird 1:1 kopiert.
     *
     * @param is {@link InputStream}
     * @param mimeType String
     *
     * @throws IOException Falls was schiefgeht.
     */
    public ByteArrayDataSource(final InputStream is, final String mimeType) throws IOException
    {
        super();

        Objects.requireNonNull(is, "inputStream required");

        this.mimeType = Objects.requireNonNull(mimeType, "mimeType required");

        final byte[] bytes = new byte[4096];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (true)
        {
            final int bytesRead = is.read(bytes);

            if (bytesRead > -1)
            {
                baos.write(bytes, 0, bytesRead);
            }
            else
            {
                // no more data...
                break;
            }
        }

        this.data = baos.toByteArray();
    }

    /**
     * Create a {@link ByteArrayDataSource} from an Object.
     *
     * @param object Serializable
     *
     * @throws IOException Falls was schiefgeht.
     */
    public ByteArrayDataSource(final Serializable object) throws IOException
    {
        this(ByteUtils.serializeObject(object), MIMETYPE_APPLICATION_OCTET_STREAM);
    }

    /**
     * Create a {@link ByteArrayDataSource} from a String.
     *
     * @param value String
     * @param mimeType String
     *
     * @throws UnsupportedEncodingException Falls was schiefgeht.
     */
    public ByteArrayDataSource(final String value, final String mimeType) throws UnsupportedEncodingException
    {
        super();

        if (value != null)
        {
            // Assumption that the string contains only ASCII characters!
            // Otherwise, just pass a charset into this
            // constructor and use it in getBytes()
            this.data = value.getBytes(StandardCharsets.ISO_8859_1);
        }
        else
        {
            this.data = null;
        }

        this.mimeType = Objects.requireNonNull(mimeType, "mimeType required");
    }

    /**
     * @see javax.activation.DataSource#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.mimeType;
    }

    /**
     * @see javax.activation.DataSource#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException
    {
        if (this.data == null)
        {
            throw new IOException("no data");
        }

        return new ByteArrayInputStream(this.data);
    }

    /**
     * @see javax.activation.DataSource#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see javax.activation.DataSource#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException
    {
        if (this.data == null)
        {
            throw new IOException("no data");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(this.data);

        return baos;
    }

    /**
     * Setzt den Namen der DataSource.
     *
     * @param value String
     */
    public void setName(final String value)
    {
        if (value != null)
        {
            this.name = value;
        }
    }
}
