package de.freese.base.demo.nasa.bp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageReader;
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
	 * @param url {@link URL}
	 * @return {@link ImageReader}
	 * @throws IOException Falls was schief geht.
	 */
	public ImageReader findImageReader(final URL url) throws IOException;

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
	 * @param reader {@link ImageReader}
	 * @param listener {@link IIOReadProgressListener}
	 * @return {@link BufferedImage}
	 * @throws IOException Falls was schief geht.
	 */
	public BufferedImage loadImage(final ImageReader reader, final IIOReadProgressListener listener)
		throws IOException;
}
