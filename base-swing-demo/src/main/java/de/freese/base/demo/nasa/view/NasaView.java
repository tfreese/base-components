// Created: 07.02.23
package de.freese.base.demo.nasa.view;

import java.awt.image.BufferedImage;
import java.net.URL;

import de.freese.base.mvc.view.View;

/**
 * @author Thomas Freese
 */
public interface NasaView extends View
{
    void setImage(URL url, BufferedImage image);

    void setMessage(String key, URL url, Throwable throwable);
}
