package de.freese.base.demo.nasa.controller;

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
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FilenameUtils;
import de.freese.base.demo.nasa.task.NasaImageTask;
import de.freese.base.demo.nasa.view.DefaultNasaView;
import de.freese.base.demo.nasa.view.NasaPanel;
import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.controller.AbstractController;
import de.freese.base.mvc.controller.Controller;
import de.freese.base.swing.task.AbstractSwingTask;
import de.freese.base.swing.task.inputblocker.DefaultInputBlocker;

/**
 * {@link Controller} des Nasa Beispiels.
 *
 * @author Thomas Freese
 */
public class NasaController extends AbstractController
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
     *
     */
    private final NasaView view;

    /**
     * Erstellt ein neues {@link NasaController} Object.
     *
     * @param context {@link ApplicationContext}
     */
    public NasaController(final ApplicationContext context)
    {
        super(context);

        // this.messageDigest = createMessageDigest();

        this.view = new DefaultNasaView(context);
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
     * @see de.freese.base.mvc.controller.AbstractController#getBundleName()
     */
    @Override
    protected String getBundleName()
    {
        return "bundles/nasa";
    }

    /**
     * @see de.freese.base.mvc.controller.Controller#getName()
     */
    @Override
    public String getName()
    {
        return "nasa";
    }

    /**
     * @return {@link URL}
     * @throws MalformedURLException Falls was schief geht.
     */
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
     * @return {@link URL}
     * @throws MalformedURLException Falls was schief geht.
     */
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
     * @see de.freese.base.mvc.controller.Controller#getView()
     */
    @SuppressWarnings("unchecked")
    @Override
    public NasaView getView()
    {
        return this.view;
    }

    /**
     * @see de.freese.base.mvc.controller.AbstractController#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();

        NasaPanel nasaPanel = getView().getComponent();

        nasaPanel.getButtonPrevious().addActionListener(event -> {
            NasaImageTask task = new NasaImageTask(this, () -> getPreviousURL(), getView(), getResourceMap());
            task.setInputBlocker(new DefaultInputBlocker().add(nasaPanel.getButtonNext(), nasaPanel.getButtonPrevious()));

            getContext().getTaskManager().execute(task);
        });

        nasaPanel.getButtonNext().addActionListener(event -> {
            NasaImageTask task = new NasaImageTask(this, () -> getNextURL(), getView(), getResourceMap());
            task.setInputBlocker(new DefaultInputBlocker().add(nasaPanel.getButtonNext(), nasaPanel.getButtonPrevious()));

            getContext().getTaskManager().execute(task);
        });

        nasaPanel.getButtonCancel().addActionListener(event -> {
            AbstractSwingTask<?, ?> task = getContext().getTaskManager().getForegroundTask();

            if (task != null)
            {
                task.cancel(true);
            }
        });
    }

    /**
     * @param url {@link URL}
     * @param listener {@link IIOReadProgressListener}
     * @return {@link BufferedImage}
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public BufferedImage loadImage(final URL url, final IIOReadProgressListener listener) throws Exception
    {
        // ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
        // ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();

        Path urlPath = Paths.get(url.getPath());
        String fileName = urlPath.getFileName().toString();
        String extension = FilenameUtils.getExtension(fileName);
        // Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".") + 1));
        String cachedFileName = "images/" + fileName;
        Path cachePath = getContext().getLocalStorage().getPath(cachedFileName);

        boolean cacheFileExist = Files.exists(cachePath);

        InputStream inputStream = null;

        if (cacheFileExist)
        {
            getLogger().info("URL load from: {}", cachePath);
            inputStream = getContext().getLocalStorage().getInputStream(cachedFileName);
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
            try (OutputStream outputStream = getContext().getLocalStorage().getOutputStream(cachedFileName))
            {
                ImageIO.write(image, extension, outputStream);

                outputStream.flush();
            }

            getLogger().info("URL saved in: {}", cachePath);
        }

        inputStream.close();

        return image;
    }
}
