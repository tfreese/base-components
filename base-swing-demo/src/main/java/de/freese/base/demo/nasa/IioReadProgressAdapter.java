package de.freese.base.demo.nasa;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

/**
 * @author Thomas Freese
 */
public class IioReadProgressAdapter implements IIOReadProgressListener {
    /**
     * @see IIOReadProgressListener#imageComplete(ImageReader)
     */
    @Override
    public void imageComplete(final ImageReader source) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#imageProgress(ImageReader, float)
     */
    @Override
    public void imageProgress(final ImageReader source, final float percentageDone) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#imageStarted(ImageReader, int)
     */
    @Override
    public void imageStarted(final ImageReader source, final int imageIndex) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#readAborted(ImageReader)
     */
    @Override
    public void readAborted(final ImageReader source) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#sequenceComplete(ImageReader)
     */
    @Override
    public void sequenceComplete(final ImageReader source) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#sequenceStarted(ImageReader, int)
     */
    @Override
    public void sequenceStarted(final ImageReader source, final int minIndex) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#thumbnailComplete(ImageReader)
     */
    @Override
    public void thumbnailComplete(final ImageReader source) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#thumbnailProgress(ImageReader, float)
     */
    @Override
    public void thumbnailProgress(final ImageReader source, final float percentageDone) {
        // Empty
    }

    /**
     * @see IIOReadProgressListener#thumbnailStarted(ImageReader, int, int)
     */
    @Override
    public void thumbnailStarted(final ImageReader source, final int imageIndex, final int thumbnailIndex) {
        // Empty
    }
}
