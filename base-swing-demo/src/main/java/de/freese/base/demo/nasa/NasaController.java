// Created: 07.02.23
package de.freese.base.demo.nasa;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.mvc.controller.AbstractController;
import de.freese.base.mvc.storage.LocalStorage;

/**
 * @author Thomas Freese
 */
public class NasaController extends AbstractController {
    private static final String IMAGE_DIR = "https://photojournal.jpl.nasa.gov/jpeg/";

    private final String[] imageNames = {
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
            "PIA05990.jpg"};
    private final Random random = new Random();
    private final List<URI> uriHistory = new ArrayList<>();

    private int uriHistoryCurrentIndex = -1;

    public NasaController(final NasaView view) {
        super(view);
    }

    public URI getNextUri() throws Exception {
        // if (++this.uriHistoryCurrentIndex == this.imageNames.length)
        // {
        // this.uriHistoryCurrentIndex = 0;
        // }

        this.uriHistoryCurrentIndex++;
        URI uri = null;

        // Bin ich am Ende der History ?
        if (this.uriHistoryCurrentIndex < this.uriHistory.size()) {
            uri = this.uriHistory.get(this.uriHistoryCurrentIndex);
        }
        else {
            uri = generateUri();
            this.uriHistory.add(uri);
        }

        return uri;
    }

    public URI getPreviousUri() throws Exception {
        // if (--this.uriHistoryCurrentIndex < 0)
        // {
        // this.uriHistoryCurrentIndex = this.imageNames.length - 1;
        // }

        this.uriHistoryCurrentIndex--;
        URI uri = null;

        // Bin ich am Anfang der History ?
        if (this.uriHistoryCurrentIndex >= 0) {
            uri = this.uriHistory.get(this.uriHistoryCurrentIndex);
        }
        else {
            uri = generateUri();
            this.uriHistory.addFirst(uri);
            this.uriHistoryCurrentIndex = 0;
        }

        return uri;
    }

    @Override
    public NasaView getView() {
        return (NasaView) super.getView();
    }

    public BufferedImage loadImage(final LocalStorage localStorage, final URI uri, final IIOReadProgressListener listener) throws Exception {
        // final ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();
        // final ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();

        final Path path = Paths.get(uri.getPath());
        final String fileName = path.getFileName().toString();
        final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        // Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".") + 1));
        final Path cachedFileName = Paths.get("images", fileName);
        final Path cachePath = localStorage.getAbsolutPath(cachedFileName);

        final boolean cacheFileExist = Files.exists(cachePath);

        InputStream inputStream = null;

        if (cacheFileExist) {
            getLogger().info("Load from: {}", cachePath);
            inputStream = localStorage.getInputStream(cachedFileName);
        }
        else {
            inputStream = uri.toURL().openStream();
        }

        // byte[] digest = this.messageDigest.digest(uri.toString().getBytes(StandardCharsets.UTF_8));
        // String hex = Hex.encodeHexString(digest, false);

        ImageReader reader = null;
        BufferedImage image = null;

        try (ImageInputStream iis = ImageIO.createImageInputStream(inputStream)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers != null && readers.hasNext()) {
                reader = readers.next();
            }
            else {
                throw new IllegalStateException("no image reader");
            }

            reader.setInput(iis);

            if (listener != null) {
                reader.addIIOReadProgressListener(listener);
            }

            final int index = reader.getMinIndex();
            image = reader.read(index);
        }
        finally {
            if (reader != null) {
                reader.removeAllIIOReadProgressListeners();
                reader.dispose();
            }
        }

        if (image != null && !cacheFileExist) {
            try (OutputStream outputStream = localStorage.getOutputStream(cachedFileName)) {
                ImageIO.write(image, extension, outputStream);

                outputStream.flush();
            }

            getLogger().info("Saved in: {}", cachePath);
        }

        inputStream.close();

        return image;
    }

    protected boolean exist(final URI uri) {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) uri.toURL().openConnection();
            httpURLConnection.setRequestMethod("HEAD");

            final int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
        }
        catch (Exception ex) {
            getLogger().warn("URI not exist: {}", uri);
        }

        return false;
    }

    protected URI generateUri() throws Exception {
        String uriString = null;
        final boolean randomUris = true;

        if (!randomUris) {
            final int index = this.random.nextInt(this.imageNames.length);
            uriString = IMAGE_DIR + this.imageNames[index];
        }
        else {
            uriString = String.format("%sPIA%05d.jpg", IMAGE_DIR, this.random.nextInt(12196) + 1);
        }

        getLogger().info("URI: {}", uriString);

        URI uri = new URI(uriString);

        if (!exist(uri)) {
            getLogger().warn("Not exist: {}", uri);
            uri = generateUri();
        }

        if (!exist(uri)) {
            getLogger().warn("Not exist: {}", uri);
            uri = generateUri();
        }

        return uri;
    }
}
