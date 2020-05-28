package de.freese.base.demo.nasa.view;

import java.awt.image.BufferedImage;
import java.net.URL;
import de.freese.base.mvc.view.View;

/**
 * @author Thomas Freese
 */
public interface NasaView extends View<NasaPanel>
{
    /**
     * @param url {@link URL}
     * @param image {@link BufferedImage}
     */
    public void setImage(URL url, BufferedImage image);

    /**
     * @param key String
     * @param url {@link URL}
     * @param throwable {@link Throwable}, optional
     */
    public void setMessage(String key, URL url, Throwable throwable);
}
