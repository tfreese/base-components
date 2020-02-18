package de.freese.base.demo.nasa.task;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

import de.freese.base.demo.nasa.bp.IIOReadProgressAdapter;
import de.freese.base.demo.nasa.bp.INasaBP;
import de.freese.base.demo.nasa.view.INasaView;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.task.AbstractTask;

/**
 * Task fuer die Nasa Demo.
 * 
 * @author Thomas Freese
 */
public class NasaImageTask extends AbstractTask<BufferedImage, Void>
{
	/**
	 *
	 */
	private final INasaBP nasaBP;

	/**
	 *
	 */
	private final URL url;

	/**
	 *
	 */
	private final INasaView view;

	/**
	 * 
	 */
	private final IResourceMap resourceMap;

	/**
	 * Erstellt ein neues {@link NasaImageTask} Object.
	 * 
	 * @param nasaBP {@link INasaBP}
	 * @param url {@link URL}
	 * @param view {@link INasaView}
	 * @param resourceMap {@link IResourceMap}
	 */
	public NasaImageTask(final INasaBP nasaBP, final URL url, final INasaView view,
			final IResourceMap resourceMap)
	{
		super();

		this.nasaBP = nasaBP;
		this.url = url;
		this.view = view;
		this.resourceMap = resourceMap;
	}

	/**
	 * @see de.freese.base.swing.task.AbstractTask#cancelled()
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

		setSubTitle(this.resourceMap.getString("nasa.load.start", this.url));

		IIOReadProgressListener rpl = new IIOReadProgressAdapter()
		{
			/**
			 * @see de.freese.base.demo.nasa.bp.IIOReadProgressAdapter#imageProgress(javax.imageio.ImageReader,
			 *      float)
			 */
			@Override
			public void imageProgress(final ImageReader source, final float percentageDone)
			{
				setProgress(percentageDone, 0.0f, 100.0f);
			}
		};

		ImageReader imageReader = this.nasaBP.findImageReader(this.url);

		return this.nasaBP.loadImage(imageReader, rpl);
	}

	/**
	 * @see de.freese.base.swing.task.AbstractTask#failed(java.lang.Throwable)
	 */
	@Override
	protected void failed(final Throwable cause)
	{
		super.failed(cause);

		this.view.setMessage("nasa.load.failed", this.url, cause);
	}

	/**
	 * @see de.freese.base.swing.task.AbstractTask#succeeded(java.lang.Object)
	 */
	@Override
	protected void succeeded(final BufferedImage result)
	{
		this.view.setImage(this.url, result);

		getLogger().info("Succeeded");
	}
}
