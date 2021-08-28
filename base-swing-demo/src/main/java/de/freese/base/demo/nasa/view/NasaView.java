package de.freese.base.demo.nasa.view;

import java.awt.image.BufferedImage;
import java.net.URL;

import de.freese.base.mvc.View;

/**
 * @author Thomas Freese
 */
public interface NasaView extends View
{
    /**
     * @see de.freese.base.mvc.View#getComponent()
     */
    @Override
    NasaPanel getComponent();

    /**
     * @param url {@link URL}
     * @param image {@link BufferedImage}
     */
    void setImage(URL url, BufferedImage image);

    /**
     * @param key String
     * @param url {@link URL}
     * @param throwable {@link Throwable}, optional
     */
    void setMessage(String key, URL url, Throwable throwable);
}
