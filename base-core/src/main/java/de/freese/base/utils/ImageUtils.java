package de.freese.base.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import de.freese.base.core.image.BlackWhiteOp;
import de.freese.base.core.image.ImageFormat;

/**
 * Nützliches für die Bildverarbeitung.
 *
 * @author Thomas Freese
 */
public final class ImageUtils
{
    /**
     * Leeres Icon Rechteck ohne Pixel, - Größe 16 x 16 Pixel ist Standard.
     *
     * @author Thomas Freese
     */
    private static final class EmptyIcon extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = 102999713634663152L;

        /**
         *
         */
        private final int height;
        /**
         *
         */
        private final int width;

        /**
         * Creates a new {@link EmptyIcon} object.
         */
        private EmptyIcon()
        {
            this(16, 16);
        }

        /**
         * Creates a new {@link EmptyIcon} object.
         *
         * @param width int
         * @param height int
         */
        private EmptyIcon(final int width, final int height)
        {
            super();

            this.width = width;
            this.height = height;

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return this.height;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return this.width;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            // NOOP
        }
    }

    /**
     * Icon, wenn kein Icon da ist Rechteck mit Kreuz drin (Rot) - Größe 16 x 16 Pixel.
     *
     * @author Thomas Freese
     */
    private static final class MissingIcon extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = -3986977626709987448L;

        /**
         * Creates a new {@link MissingIcon} object.
         */
        private MissingIcon()
        {
            super();

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return 16;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return 16;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.getRenderingHintsQuality());

            g.setColor(Color.RED);
            g.translate(x, y);
            g.drawLine(2, 2, 13, 13);
            g.drawLine(13, 2, 2, 13);
            g.drawRect(2, 2, 11, 11);
            g.translate(-x, -y);
        }
    }

    /**
     * Icon für ein Pfeil Zeichen.
     *
     * @author Thomas Freese
     */
    private static final class Triangle extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = 6491045895051309036L;

        /**
         *
         */
        private final int direction;
        /**
         *
         */
        private final Color foreground;
        /**
        *
        */
        private final int height;
        /**
        *
        */
        private final int width;

        /**
         * Creates a new {@link Triangle} object.<br>
         * Defaults: Widht = 16, Height = 16, ForeGround = Black
         *
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         */
        private Triangle(final int direction)
        {
            this(16, 16, direction, Color.BLACK);
        }

        /**
         * Creates a new {@link Triangle} object.
         *
         * @param width int
         * @param height int
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         * @param foreground {@link Color}
         *
         * @throws IllegalArgumentException Falls Direction ungültig
         */
        private Triangle(final int width, final int height, final int direction, final Color foreground)
        {
            super();

            this.width = width;
            this.height = height;
            this.direction = direction;
            this.foreground = foreground;

            if ((this.direction != SwingConstants.NORTH) && (this.direction != SwingConstants.SOUTH) && (this.direction != SwingConstants.EAST)
                    && (this.direction != SwingConstants.WEST))
            {
                throw new IllegalArgumentException("Only SwingConstants.NORTH, SOUTH, EAST, WEST supported !");
            }

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return this.height;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return this.width;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.getRenderingHintsQuality());
            g2d.setColor(this.foreground);
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int[] xPoints;
            int[] yPoints;

            if (this.direction == SwingConstants.NORTH)
            {
                xPoints = new int[]
                {
                        x, x + centerX, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y + (centerY * 2), y, y + (centerY * 2)
                };
            }
            else if (this.direction == SwingConstants.SOUTH)
            {
                xPoints = new int[]
                {
                        x, x + centerX, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y, y + (centerY * 2), y
                };
            }
            else if (this.direction == SwingConstants.WEST)
            {
                xPoints = new int[]
                {
                        x + (centerX * 2), x, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y, y + centerY, y + (centerY * 2)
                };
            }
            else if (this.direction == SwingConstants.EAST)
            {
                xPoints = new int[]
                {
                        x, x + (centerX * 2), x, x
                };
                yPoints = new int[]
                {
                        y, y + centerY, y + (centerY * 2)
                };
            }
            else
            {
                throw new IllegalStateException();
            }

            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Erzeugt ein leeres Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon ohne Inhalt
     */
    public static ImageIcon createEmptyIcon()
    {
        return new EmptyIcon();
    }

    /**
     * Erzeugt ein leeres Icon der Größe x mal y.
     *
     * @param width int Breite
     * @param height int Höhe
     *
     * @return {@link ImageIcon}, Ein Icon ohne Inhalt
     */
    public static ImageIcon createEmptyIcon(final int width, final int height)
    {
        return new EmptyIcon(width, height);
    }

    /**
     * Erzeugt ein Missing-Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon (Roter Kasten mit rotem X)
     */
    public static ImageIcon createMissingIcon()
    {
        return new MissingIcon();
    }

    /**
     * Liefert ein TriangleIcon mit einer Größe von 16x16 Pixel und schwarzem Vordergrund.
     *
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     *
     * @return {@link ImageIcon}
     */
    public static ImageIcon createTriangleIcon(final int direction)
    {
        return new Triangle(direction);
    }

    /**
     * Liefert ein TriangleIcon.
     *
     * @param width int
     * @param height int
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     * @param foreground int
     *
     * @return {@link ImageIcon}
     */
    public static ImageIcon createTriangleIcon(final int width, final int height, final int direction, final Color foreground)
    {
        return new Triangle(width, height, direction, foreground);
    }

    /**
     * Encode the image in a specific format.
     *
     * @param image The image to be encoded.
     * @param format {@link ImageFormat}
     *
     * @return The byte[] that is the encoded image.
     *
     * @throws IOException Falls was schief geht
     */
    public static byte[] encode(final Image image, final ImageFormat format) throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            writeImage(image, format, baos);

            baos.flush();

            return baos.toByteArray();
        }
    }

    /**
     * @return {@link RenderingHints}
     */
    public static RenderingHints getRenderingHintsQuality()
    {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return hints;
    }

    /**
     * Liefert true, wenn das {@link Image} transparente Pixel enthält.
     *
     * @param image {@link Image}
     *
     * @return boolean
     */
    public static boolean hasAlpha(final Image image)
    {
        if (image instanceof BufferedImage bimage)
        {
            return bimage.getColorModel().hasAlpha();
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

        try
        {
            pg.grabPixels();
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }

    /**
     * Vereint zwei Bilder in einem. dabei wird das zweite über das erste Bild gelegt.
     *
     * @param image {@link Image}
     * @param overlay {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage merge(final Image image, final Image overlay)
    {
        int width = Math.max(image.getWidth(null), overlay.getWidth(null));
        int height = Math.max(image.getHeight(null), overlay.getHeight(null));

        BufferedImage merged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = merged.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.drawImage(overlay, 0, 0, null);

        return merged;
    }

    /**
     * Vereint zwei Bilder in einem, dabei wird das zweite über das erste Bild gelegt.
     *
     * @param image {@link ImageIcon}
     * @param overlay {@link ImageIcon}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage merge(final ImageIcon image, final ImageIcon overlay)
    {
        return merge(image.getImage(), overlay.getImage());
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     *
     * @param src {@link BufferedImage}
     * @param width int
     * @param height int
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImage(final Image src, final int width, final int height)
    {
        Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return toBufferedImage(scaled);
    }

    /**
     * Skaliert das Bild auf die neuen Seitenverhältnisse.
     *
     * @param src {@link Image}
     * @param ratioWidth double
     * @param ratioHeight double
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImageByRatio(final Image src, final double ratioWidth, final double ratioHeight)
    {
        BufferedImage bufferedImage = toBufferedImage(src);

        AffineTransform tx = new AffineTransform();
        tx.scale(ratioWidth, ratioHeight);

        // tx.shear(shiftx, shifty);
        // tx.translate(x, y);
        // tx.rotate(radians, origin.getWidth()/2, origin.getHeight()/2);

        RenderingHints hints = getRenderingHintsQuality();

        AffineTransformOp op = new AffineTransformOp(tx, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Skaliert das Bild unter Beibehaltung des Seitenverhältnisses bis auf die maximale angegebene Höhe oder Breite.
     *
     * @param src {@link Image}
     * @param maxWidth int
     * @param maxHeight int
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImageKeepRatio(final Image src, final int maxWidth, final int maxHeight)
    {
        BufferedImage bufferedImage = toBufferedImage(src);

        double widthRatio = (double) maxWidth / bufferedImage.getWidth();
        double heightRatio = (double) maxHeight / bufferedImage.getHeight();

        double ratio = Math.min(widthRatio, heightRatio);

        return scaleImageByRatio(bufferedImage, ratio, ratio);

        // double newWidth = bufferedImage.getWidth() * ratio;
        // double newHeight = bufferedImage.getHeight() * ratio;
        //
        // return scaleImage(bufferedImage, (int) newWidth, (int) newHeight);
    }

    /**
     * Liefert das Schwarzweiss Bild.
     *
     * @param image {@link BufferedImage}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBlackWhiteImage(final BufferedImage image)
    {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImageOp op;

        // int width = getSourceImage().getWidth();
        // int height = getSourceImage().getHeight();
        // ColorModel colorModel = ColorModel.getRGBdefault();
        //
        // BufferedImage blackWhiteImage = new BufferedImage(width, height,
        // BufferedImage.TYPE_INT_RGB);
        // byte[] data = new byte[256];
        // Arrays.fill(data, (byte) 255);
        // data[0] = (byte)0;
        // data[254] = (byte)255;
        // data[253] = (byte)255;
        // data[252] = (byte)255;
        // data[251] = (byte)255;
        // data[250] = (byte)255;
        //
        // IndexColorModel colorModel = new IndexColorModel(2, 256, data, data, data);
        //
        // blackWhiteImage = new BufferedImage(width, height,
        // BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        //
        // Graphics2D g2d = this.blackWhiteImage.createGraphics();
        // g2d.drawRenderedImage(getEdgeImage(), null);
        // g2d.dispose();
        //
        //
        // short[] red = new short[256];
        // short[] green = new short[256];
        // short[] blue = new short[256];
        //
        // for (int i = 0; i < 255; i++)
        // {
        // red[i]=255;
        // green[i]=255;
        // blue[i]=255;
        // }
        //
        // red[0]=0;
        // green[0]=0;
        // blue[0]=0;
        //
        // short[][] data = new short[][] {
        // red, green, blue
        // };
        //
        // LookupTable lookupTable = new ShortLookupTable(0, data);
        // op = new LookupOp(lookupTable, hints);
        //
        //
        // float[] factors = new float[] {
        // 1000f, 1000f, 1000f
        // };
        // float[] offsets = new float[] {
        // 0.0f, 0.0f, 0.0f
        // };
        // BufferedImageOp op = new RescaleOp(factors, offsets, hints);
        op = new BlackWhiteOp(hints, 0);

        return op.filter(image, null);
    }

    /**
     * Liefert das Schwarzweiss Bild.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBlackWhiteImage(final Image image)
    {
        BufferedImage bufferedImage = toBufferedImage(image);

        return toBlackWhiteImage(bufferedImage);
    }

    /**
     * Kopiert ein Icon in eine Image-Kopie.
     *
     * @param icon {@link Icon}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(final Icon icon)
    {
        if (icon instanceof ImageIcon imageIcon)
        {
            if (imageIcon.getImage()instanceof BufferedImage bi)
            {
                return bi;
            }
        }

        BufferedImage returnImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = returnImage.getGraphics();

        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        return returnImage;
    }

    /**
     * Konvertiert ein {@link Image} in ein {@link BufferedImage}.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(final Image image)
    {
        if (image instanceof BufferedImage bi)
        {
            return bi;
        }

        BufferedImage bufferedImage;

        // boolean hasAlpha = hasAlpha(image);
        //
        // try
        // {
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //
        // int transparency = Transparency.OPAQUE;
        //
        // if (hasAlpha)
        // {
        // transparency = Transparency.BITMASK;
        // }
        //
        // GraphicsDevice gs = ge.getDefaultScreenDevice();
        // GraphicsConfiguration gc = gs.getDefaultConfiguration();
        //
        // bufferedImage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        // }
        // catch (HeadlessException ex)
        // {
        // // Keine GUI vorhanden
        // }
        //
        // if (bufferedImage == null)
        // {
        // int type = BufferedImage.TYPE_INT_RGB;
        //
        // if (hasAlpha)
        // {
        // type = BufferedImage.TYPE_INT_ARGB;
        // }
        //
        // bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        // }

        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        RenderingHints hints = getRenderingHintsQuality();

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHints(hints);

        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
    }

    /**
     * Liefert das Kanten Bild.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toEdgeImage(final Image image)
    {
        BufferedImage bufferedImage = toBufferedImage(image);

        // Sobel Operator, horizontal & vertikal
        float[] matrix =
        {
                0.0F, -1.0F, 0.0F, -1.0F, 4.0F, -1.0F, 0.0F, -1.0F, 0.0F
        };

        // // Sobel Operator, horizontal
        // float[] matrix = new float[]
        // {
        // 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f
        // };
        // // Sobel Operator, vertikal
        // float[] matrix = new float[]
        // {
        // 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f
        // };
        // // Sobel Operator, diagonal
        // float[] matrix =
        // {
        // -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f
        // };

        Kernel kernel = new Kernel(3, 3, matrix);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Graut das Icon aus.
     *
     * @param icon {@link Icon}
     *
     * @return {@link Icon}
     */
    public static Icon toGrayIcon(final Icon icon)
    {
        BufferedImage bufferedImage = toBufferedImage(icon);
        Image result = toGrayImage(bufferedImage);

        return new ImageIcon(result);
    }

    /**
     * Graut das Icon aus.
     *
     * @param imageIcon ImageIcon
     * @param percent int
     *
     * @return {@link ImageIcon}
     */
    public static ImageIcon toGrayIcon(final ImageIcon imageIcon, final int percent)
    {
        Image image = toGrayImage(imageIcon.getImage(), percent);

        return new ImageIcon(image);
    }

    /**
     * Erzeugt ein Graustufenbild.
     *
     * @param bufferedImage {@link BufferedImage}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toGrayImage(final BufferedImage bufferedImage)
    {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ColorConvertOp op = new ColorConvertOp(colorSpace, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Graut das Bild aus.
     *
     * @param image ImageIcon
     * @param percent int, 0-100%
     *
     * @return {@link Image}
     */
    public static Image toGrayImage(final Image image, final int percent)
    {
        ImageFilter filter = new GrayFilter(true, percent);
        ImageProducer prod = new FilteredImageSource(image.getSource(), filter);

        return Toolkit.getDefaultToolkit().createImage(prod);
    }

    /**
     * Liefert das geschärfte Bild.
     *
     * @param image {@link BufferedImage}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toSharpenImage(final BufferedImage image)
    {
        float[] matrix =
        {
                0.0F, -1.0F, 0.0F, -1.0F, 5.0F, -1.0F, 0.0F, -1.0F, 0.0F
        };

        // Kantenglaettung
        // float[] matrix =
        // new float[]
        // {
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f
        // };
        Kernel kernel = new Kernel(3, 3, matrix);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(image, null);
    }

    /**
     * Liefert das geschärfte Bild.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toSharpenImage(final Image image)
    {
        BufferedImage bufferedImage = toBufferedImage(image);

        return toSharpenImage(bufferedImage);
    }

    /**
     * Encode the image in a specific format and write it to an OutputStream.
     *
     * @param image {@link BufferedImage}
     * @param format {@link ImageFormat}
     * @param outputStream {@link OutputStream}
     *
     * @throws IOException Falls was schief geht
     */
    public static void writeImage(final BufferedImage image, final ImageFormat format, final OutputStream outputStream) throws IOException
    {
        ImageIO.write(image, format.name(), outputStream);
    }

    /**
     * Encode the image in a specific format and write it to an OutputStream.
     *
     * @param image {@link Image}
     * @param format {@link ImageFormat}
     * @param outputStream {@link OutputStream}
     *
     * @throws IOException Falls was schief geht
     */
    public static void writeImage(final Image image, final ImageFormat format, final OutputStream outputStream) throws IOException
    {
        BufferedImage bufferedImage = toBufferedImage(image);

        writeImage(bufferedImage, format, outputStream);
    }

    /**
     * Erstellt ein neues {@link ImageUtils} Object.
     */
    private ImageUtils()
    {
        super();
    }
}
