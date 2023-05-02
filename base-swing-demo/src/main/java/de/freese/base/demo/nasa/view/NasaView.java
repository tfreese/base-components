// Created: 07.02.23
package de.freese.base.demo.nasa.view;

import java.awt.image.BufferedImage;
import java.net.URI;

import de.freese.base.mvc.view.View;

/**
 * @author Thomas Freese
 */
public interface NasaView extends View {
    void setImage(URI uri, BufferedImage image);

    void setMessage(String key, URI uri, Throwable throwable);
}
