// Created: 17.07.2008
package de.freese.base.core.image;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * Erstellt ein reines Schwarz/Weiß Bild, indem alle Pixel die NICHT Schwarz sind in Weiß umgewandelt werden.
 *
 * @author Thomas Freese
 */
public class BlackWhiteOp implements BufferedImageOp
{
    /**
     *
     */
    private final int colorLimit;
    /**
     *
     */
    private final RenderingHints hints;

    /**
     * Creates a new {@link BlackWhiteOp} object.
     */
    public BlackWhiteOp()
    {
        this(0);
    }

    /**
     * Creates a new BlackWhiteOp object.
     *
     * @param colorLimit int, wenn eine Farbe > colorLimit wird sie als Weiß interpretiert.
     */
    public BlackWhiteOp(final int colorLimit)
    {
        this(null, colorLimit);
    }

    /**
     * Creates a new {@link BlackWhiteOp} object.
     *
     * @param hints {@link RenderingHints}
     * @param colorLimit int, wenn eine Farbe > colorLimit wird sie als Weiß interpretiert
     */
    public BlackWhiteOp(final RenderingHints hints, final int colorLimit)
    {
        super();

        this.hints = hints;
        this.colorLimit = colorLimit;
    }

    /**
     * @see java.awt.image.BufferedImageOp#createCompatibleDestImage(java.awt.image.BufferedImage, java.awt.image.ColorModel)
     */
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, final ColorModel destCM)
    {
        BufferedImage image;

        ColorModel colorModel = destCM;

        if (colorModel == null)
        {
            colorModel = src.getColorModel();
        }
        else
        {
            colorModel = ColorModel.getRGBdefault();
        }

        int w = src.getWidth();
        int h = src.getHeight();
        image = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(w, h), colorModel.isAlphaPremultiplied(), null);

        return image;
    }

    /**
     * @see java.awt.image.BufferedImageOp#filter(java.awt.image.BufferedImage, java.awt.image.BufferedImage)
     */
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dest)
    {
        if (src == null)
        {
            throw new NullPointerException("src image");
        }

        if (src == dest)
        {
            throw new IllegalArgumentException("src image cannot be the " + "same as the dst image");
        }

        BufferedImage destImage = dest;

        if (dest == null)
        {
            destImage = createCompatibleDestImage(src, null);
        }

        int width = src.getWidth();
        int height = src.getHeight();

        int rgbBlack = Color.BLACK.getRGB();
        int rgbWhite = Color.WHITE.getRGB();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int pixel = src.getRGB(x, y);

                @SuppressWarnings("unused")
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel >> 0) & 0xff;

                if ((red > this.colorLimit) || (green > this.colorLimit) || (blue > this.colorLimit))
                {
                    // int rgb = ((255 & 0xFF) << 24) | ((255 & 0xFF) << 16) | ((255 & 0xFF) << 8) | ((255 & 0xFF) << 0);
                    destImage.setRGB(x, y, rgbWhite);
                }
                else
                {
                    destImage.setRGB(x, y, rgbBlack);
                }
            }
        }

        return destImage;
    }

    /**
     * @see java.awt.image.BufferedImageOp#getBounds2D(java.awt.image.BufferedImage)
     */
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src)
    {
        return src.getRaster().getBounds();
    }

    /**
     * @see java.awt.image.BufferedImageOp#getPoint2D(java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    @Override
    public Point2D getPoint2D(final Point2D srcPt, final Point2D dstPt)
    {
        Point2D point2d = dstPt;

        if (point2d == null)
        {
            point2d = new Point2D.Float();
        }

        point2d.setLocation(srcPt.getX(), srcPt.getY());

        return point2d;
    }

    /**
     * @see java.awt.image.BufferedImageOp#getRenderingHints()
     */
    @Override
    public RenderingHints getRenderingHints()
    {
        return this.hints;
    }
}
