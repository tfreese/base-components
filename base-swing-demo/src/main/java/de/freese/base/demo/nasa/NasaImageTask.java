package de.freese.base.demo.nasa;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;

/**
 * @author Thomas Freese
 */
public class NasaImageTask extends AbstractSwingTask<BufferedImage, Void>
{
    /**
     *
     */
    private final NasaController nasaController;
    /**
     *
     */
    private final ResourceMap resourceMap;
    /**
     *
     */
    private final Callable<URL> urlCallable;
    /**
     *
     */
    private final NasaView view;
    /**
     *
     */
    private URL url;

    /**
     * Erstellt ein neues {@link NasaImageTask} Object.
     *
     * @param nasaController {@link NasaController}
     * @param urlCallable {@link Callable}
     * @param view {@link NasaView}
     * @param resourceMap {@link ResourceMap}
     */
    public NasaImageTask(final NasaController nasaController, final Callable<URL> urlCallable, final NasaView view, final ResourceMap resourceMap)
    {
        super();

        this.nasaController = Objects.requireNonNull(nasaController, "nasaController required");
        this.urlCallable = Objects.requireNonNull(urlCallable, "urlCallable required");
        this.view = Objects.requireNonNull(view, "view required");
        this.resourceMap = Objects.requireNonNull(resourceMap, "resourceMap required");
    }

    /**
     * @see de.freese.base.swing.task.AbstractSwingTask#cancelled()
     */
    @Override
    protected void cancelled()
    {
        this.view.setMessage("nasa.load.canceled", this.url, null);

        getLogger().info("Cancelled");
    }

    /**
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected BufferedImage doInBackground() throws Exception
    {
        getLogger().info("Started");

        this.url = this.urlCallable.call();

        this.view.setMessage("nasa.load.start", this.url, null);
        setSubTitle(this.resourceMap.getString("nasa.load.start", this.url));

        IIOReadProgressListener rpl = new IIOReadProgressAdapter()
        {
            /**
             * @see de.freese.base.demo.nasa.IIOReadProgressAdapter#imageProgress(javax.imageio.ImageReader, float)
             */
            @Override
            public void imageProgress(final ImageReader source, final float percentageDone)
            {
                setProgress(percentageDone, 0.0F, 100.0F);
            }
        };

        return this.nasaController.loadImage(this.url, rpl);
    }

    /**
     * @see de.freese.base.swing.task.AbstractSwingTask#failed(java.lang.Throwable)
     */
    @Override
    protected void failed(final Throwable cause)
    {
        super.failed(cause);

        this.view.setMessage("nasa.load.failed", this.url, cause);
    }

    /**
     * @see de.freese.base.swing.task.AbstractSwingTask#succeeded(java.lang.Object)
     */
    @Override
    protected void succeeded(final BufferedImage result)
    {
        this.view.setImage(this.url, result);

        getLogger().info("Succeeded");
    }
}
