package de.freese.base.demo.nasa.bp;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.event.IIOReadProgressListener;
import de.freese.base.mvc.process.BusinessProcess;

/**
 * Interface des BuisinessProcesses fuer das Nasa Beispiel.
 *
 * @author Thomas Freese
 */
public interface NasaBP extends BusinessProcess
{
    /**
     * @return {@link URL}
     * @throws MalformedURLException Falls was schief geht.
     */
    public URL getNextURL() throws MalformedURLException;

    /**
     * @return {@link URL}
     * @throws MalformedURLException Falls was schief geht.
     */
    public URL getPreviousURL() throws MalformedURLException;

    /**
     * @param url {@link URL}
     * @param listener {@link IIOReadProgressListener}
     * @return {@link BufferedImage}
     * @throws Exception Falls was schief geht.
     */
    public BufferedImage loadImage(final URL url, final IIOReadProgressListener listener) throws Exception;
}
