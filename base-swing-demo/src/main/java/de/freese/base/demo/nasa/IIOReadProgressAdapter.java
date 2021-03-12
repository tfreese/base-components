package de.freese.base.demo.nasa;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

/**
 * Adapter fuer den {@link IIOReadProgressListener}.
 *
 * @author Thomas Freese
 */
public class IIOReadProgressAdapter implements IIOReadProgressListener
{
    /**
     * @see javax.imageio.event.IIOReadProgressListener#imageComplete(javax.imageio.ImageReader)
     */
    @Override
    public void imageComplete(final ImageReader source)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#imageProgress(javax.imageio.ImageReader, float)
     */
    @Override
    public void imageProgress(final ImageReader source, final float percentageDone)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#imageStarted(javax.imageio.ImageReader, int)
     */
    @Override
    public void imageStarted(final ImageReader source, final int imageIndex)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#readAborted(javax.imageio.ImageReader)
     */
    @Override
    public void readAborted(final ImageReader source)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#sequenceComplete(javax.imageio.ImageReader)
     */
    @Override
    public void sequenceComplete(final ImageReader source)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#sequenceStarted(javax.imageio.ImageReader, int)
     */
    @Override
    public void sequenceStarted(final ImageReader source, final int minIndex)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#thumbnailComplete(javax.imageio.ImageReader)
     */
    @Override
    public void thumbnailComplete(final ImageReader source)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#thumbnailProgress(javax.imageio.ImageReader, float)
     */
    @Override
    public void thumbnailProgress(final ImageReader source, final float percentageDone)
    {
        // Empty
    }

    /**
     * @see javax.imageio.event.IIOReadProgressListener#thumbnailStarted(javax.imageio.ImageReader, int, int)
     */
    @Override
    public void thumbnailStarted(final ImageReader source, final int imageIndex, final int thumbnailIndex)
    {
        // Empty
    }
}
