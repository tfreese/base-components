package de.freese.base.demo.nasa;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

/**
 * @author Thomas Freese
 */
public class IioReadProgressAdapter implements IIOReadProgressListener {
    @Override
    public void imageComplete(final ImageReader source) {
        // Empty
    }

    @Override
    public void imageProgress(final ImageReader source, final float percentageDone) {
        // Empty
    }

    @Override
    public void imageStarted(final ImageReader source, final int imageIndex) {
        // Empty
    }

    @Override
    public void readAborted(final ImageReader source) {
        // Empty
    }

    @Override
    public void sequenceComplete(final ImageReader source) {
        // Empty
    }

    @Override
    public void sequenceStarted(final ImageReader source, final int minIndex) {
        // Empty
    }

    @Override
    public void thumbnailComplete(final ImageReader source) {
        // Empty
    }

    @Override
    public void thumbnailProgress(final ImageReader source, final float percentageDone) {
        // Empty
    }

    @Override
    public void thumbnailStarted(final ImageReader source, final int imageIndex, final int thumbnailIndex) {
        // Empty
    }
}
