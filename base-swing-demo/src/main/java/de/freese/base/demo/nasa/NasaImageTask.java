package de.freese.base.demo.nasa;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.task.AbstractSwingTask;

/**
 * @author Thomas Freese
 */
public class NasaImageTask extends AbstractSwingTask<BufferedImage, Void> {
    private final NasaController nasaController;
    private final ResourceMap resourceMap;
    private final Callable<URI> uriCallable;
    private final NasaView view;

    private URI uri;

    public NasaImageTask(final NasaController nasaController, final Callable<URI> uriCallable, final NasaView view, final ResourceMap resourceMap) {
        super();

        this.nasaController = Objects.requireNonNull(nasaController, "nasaController required");
        this.uriCallable = Objects.requireNonNull(uriCallable, "uriCallable required");
        this.view = Objects.requireNonNull(view, "view required");
        this.resourceMap = Objects.requireNonNull(resourceMap, "resourceMap required");
    }

    @Override
    protected void cancelled() {
        this.view.setMessage("nasa.load.canceled", this.uri, null);

        getLogger().info("Cancelled");
    }

    @Override
    protected BufferedImage doInBackground() throws Exception {
        getLogger().info("Started");

        this.uri = this.uriCallable.call();

        this.view.setMessage("nasa.load.start", this.uri, null);
        setSubTitle(this.resourceMap.getString("nasa.load.start", this.uri));

        final IIOReadProgressListener rpl = new IioReadProgressAdapter() {
            @Override
            public void imageProgress(final ImageReader source, final float percentageDone) {
                setProgress(percentageDone, 0.0F, 100.0F);
            }
        };

        return this.nasaController.loadImage(this.view.getService(LocalStorage.class), this.uri, rpl);
    }

    @Override
    protected void failed(final Throwable cause) {
        super.failed(cause);

        this.view.setMessage("nasa.load.failed", this.uri, cause);
    }

    @Override
    protected void succeeded(final BufferedImage result) {
        this.view.setImage(this.uri, result);

        getLogger().info("Succeeded");
    }
}
