package de.freese.base.demo.nasa.bp;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FilenameUtils;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.process.AbstractBusinessProcess;

/**
 * Konkreter IBusinessProcess des Nasa Beispiels.
 *
 * @author Thomas Freese
 */
public class DefaultNasaBP extends AbstractBusinessProcess implements NasaBP
{
    // /**
    // * Erzeugt einen MessageDigest.<br>
    // * Beim Auftreten einer {@link NoSuchAlgorithmException} wird diese in eine {@link RuntimeException} konvertiert.
    // *
    // * @return {@link MessageDigest}
    // * @throws RuntimeException Falls was schief geht.
    // */
    // protected static MessageDigest createMessageDigest() throws RuntimeException
    // {
    // MessageDigest messageDigest = null;
    //
    // try
    // {
    // messageDigest = MessageDigest.getInstance("SHA-256");
    // }
    // catch (final NoSuchAlgorithmException ex)
    // {
    // try
    // {
    // messageDigest = MessageDigest.getInstance("MD5");
    // }
    // catch (final NoSuchAlgorithmException ex2)
    // {
    // throw new RuntimeException(ex2);
    // }
    // }
    //
    // return messageDigest;
    // }

    // /**
    // *
    // */
    // private final Cache cache = new FileCache();

    /**
     *
     */
    private final ApplicationContext context;

    /**
     * Max. 12196 Bilder verfügbar
     */
    private final String imageDir = "https://photojournal.jpl.nasa.gov/jpeg/";

    /**
    *
    */
    private String[] imageNames =
    {
            "PIA03623.jpg",
            "PIA03171.jpg",
            "PIA02652.jpg",
            "PIA05108.jpg",
            "PIA02696.jpg",
            "PIA05049.jpg",
            "PIA05460.jpg",
            "PIA07327.jpg",
            "PIA05117.jpg",
            "PIA05199.jpg",
            "PIA05990.jpg"
    };

    // /**
    // *
    // */
    // private final MessageDigest messageDigest;

    /**
     *
     */
    private int index = -1;

    /**
     *
     */
    private final Random random = new Random();

    /**
     * Erstellt ein neues {@link DefaultNasaBP} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public DefaultNasaBP(final ApplicationContext context)
    {
        super();

        this.context = Objects.requireNonNull(context, "context required");
        // this.messageDigest = createMessageDigest();
    }

    /**
     * @param url {@link URL}
     * @return boolean
     */
    protected boolean existUrl(final URL url)
    {
        try
        {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("HEAD");

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            // Ignore
            getLogger().warn("URL not exist: {}", url);
        }

        return false;
    }

    /**
     * @see de.freese.base.demo.nasa.bp.NasaBP#getNextURL()
     */
    @Override
    public URL getNextURL() throws MalformedURLException
    {
        if (++this.index == this.imageNames.length)
        {
            this.index = 0;
        }

        // String urlString = this.imageDir + this.imageNames[this.index];
        String urlString = String.format("%sPIA%05d.jpg", this.imageDir, (this.random.nextInt(12196) + 1));

        getLogger().info("URL: {}", urlString);

        URL url = new URL(urlString);

        if (!existUrl(url))
        {
            getLogger().warn("URL not exist: {}", url);
            url = getNextURL();
        }

        return url;
    }

    /**
     * @see de.freese.base.demo.nasa.bp.NasaBP#getPreviousURL()
     */
    @Override
    public URL getPreviousURL() throws MalformedURLException
    {
        if (--this.index < 0)
        {
            this.index = this.imageNames.length - 1;
        }

        // String urlString = this.imageDir + this.imageNames[this.index];
        String urlString = String.format("%sPIA%05d.jpg", this.imageDir, (this.random.nextInt(12196) + 1));

        getLogger().info("URL: {}", urlString);

        URL url = new URL(urlString);

        if (!existUrl(url))
        {
            getLogger().warn("URL not exist: {}", url);
            url = getPreviousURL();
        }

        return url;
    }

    /**
     * @see de.freese.base.demo.nasa.bp.NasaBP#loadImage(java.net.URL, javax.imageio.event.IIOReadProgressListener)
     */
    @SuppressWarnings("resource")
    @Override
    public BufferedImage loadImage(final URL url, final IIOReadProgressListener listener) throws Exception
    {
        // ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
        // ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();

        Path urlPath = Paths.get(url.getPath());
        String fileName = urlPath.getFileName().toString();
        String extension = FilenameUtils.getExtension(fileName);
        // Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".") + 1));
        String cachedFileName = "images/" + fileName;
        Path cachePath = this.context.getLocalStorage().getPath(cachedFileName);

        boolean cacheFileExist = Files.exists(cachePath);

        InputStream inputStream = null;

        if (cacheFileExist)
        {
            getLogger().info("URL load from: {}", cachePath);
            inputStream = this.context.getLocalStorage().getInputStream(cachedFileName);
        }
        else
        {
            inputStream = url.openStream();
        }

        // byte[] digest = this.messageDigest.digest(url.toString().getBytes(StandardCharsets.UTF_8));
        // String hex = Hex.encodeHexString(digest, false);

        ImageReader reader = null;
        BufferedImage image = null;

        try (ImageInputStream iis = ImageIO.createImageInputStream(inputStream))
        {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if ((readers != null) && readers.hasNext())
            {
                reader = readers.next();
            }
            else
            {
                throw new IllegalStateException("no image reader");
            }

            reader.setInput(iis);

            if (listener != null)
            {
                reader.addIIOReadProgressListener(listener);
            }

            int index = reader.getMinIndex();
            image = reader.read(index);
        }
        finally
        {
            if (reader != null)
            {
                reader.removeAllIIOReadProgressListeners();
                reader.dispose();
            }
        }

        if ((image != null) && !cacheFileExist)
        {
            try (OutputStream outputStream = this.context.getLocalStorage().getOutputStream(cachedFileName))
            {
                ImageIO.write(image, extension, outputStream);

                outputStream.flush();
            }

            getLogger().info("URL saved in: {}", cachePath);
        }

        inputStream.close();

        return image;
    }

    /**
     * @see de.freese.base.mvc.process.AbstractBusinessProcess#release()
     */
    @Override
    public void release()
    {
        super.release();

        // this.cache.clear();
    }
}
