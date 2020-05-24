package de.freese.base.demo.nasa.bp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import de.freese.base.mvc.process.AbstractBusinessProcess;

/**
 * Konkreter IBusinessProcess des Nasa Beispiels.
 *
 * @author Thomas Freese
 */
public class DefaultNasaBP extends AbstractBusinessProcess implements NasaBP
{
    // /**
    // *
    // */
    // private final Cache cache = new FileCache();

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
     */
    public DefaultNasaBP()
    {
        super();
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
     * @see de.freese.base.demo.nasa.bp.NasaBP#findImageReader(java.net.URL)
     */
    @SuppressWarnings("resource")
    @Override
    public ImageReader findImageReader(final URL url) throws IOException
    {
        // ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
        // ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();

        ImageReader reader = null;

        ImageInputStream iis = ImageIO.createImageInputStream(url.openStream());
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

        return reader;
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

        getLogger().debug("URL: {}", urlString);

        URL url = new URL(urlString);

        if (!existUrl(url))
        {
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
            url = getPreviousURL();
        }

        return url;
    }

    /**
     * @see de.freese.base.demo.nasa.bp.NasaBP#loadImage(javax.imageio.ImageReader, javax.imageio.event.IIOReadProgressListener)
     */
    @Override
    @SuppressWarnings("resource")
    public BufferedImage loadImage(final ImageReader reader, final IIOReadProgressListener listener) throws IOException
    {
        BufferedImage image = null;

        try
        {
            if (listener != null)
            {
                reader.addIIOReadProgressListener(listener);
            }

            int index = reader.getMinIndex();
            image = reader.read(index);
        }
        finally
        {
            ImageInputStream input = (ImageInputStream) reader.getInput();

            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException xe)
                {
                    // Ignore
                }
            }

            reader.removeAllIIOReadProgressListeners();
            reader.dispose();
        }

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
