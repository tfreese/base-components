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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import de.freese.base.core.image.BlackWhiteOp;
import de.freese.base.core.image.ImageFormat;

/**
 * @author Thomas Freese
 */
public final class ImageUtils {
    /**
     * Leeres Icon Rechteck ohne Pixel, - Größe 16 x 16 Pixel ist Standard.
     *
     * @author Thomas Freese
     */
    private static final class EmptyIcon extends ImageIcon {
        @Serial
        private static final long serialVersionUID = 102999713634663152L;

        public static void main(String[] args) {
            JLabel label = new JLabel(new ImageIcon(new EmptyIcon().getImage()));

            JFrame frame = new JFrame();
            frame.add(label);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(200, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private final int iconHeight;

        private final int iconWidth;

        private transient BufferedImage bufferedImage;

        /**
         * Default: 16 x 16
         */
        private EmptyIcon() {
            this(16, 16);
        }

        private EmptyIcon(final int width, final int height) {
            super();

            this.iconWidth = width;
            this.iconHeight = height;

            setImage(null);
            //            setImage(new ImageIcon(new byte[]{0, 0}).getImage());
        }

        @Override
        public int getIconHeight() {
            return this.iconHeight;
        }

        @Override
        public int getIconWidth() {
            return this.iconWidth;
        }

        @Override
        public Image getImage() {
            if (this.bufferedImage == null) {
                this.bufferedImage = new BufferedImage(getIconWidth() + 1, getIconHeight() + 1, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = this.bufferedImage.getGraphics();
                paintIcon(null, graphics, 0, 0);
                graphics.dispose();
            }

            return this.bufferedImage;
        }

        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            // Empty
        }

        @Override
        protected void loadImage(final Image image) {
            // Empty
        }
    }

    /**
     * Icon, wenn kein Icon da ist Rechteck mit Kreuz drin (Rot) - Größe 16 x 16 Pixel.
     *
     * @author Thomas Freese
     */
    private static final class MissingIcon extends ImageIcon {
        @Serial
        private static final long serialVersionUID = -3986977626709987448L;

        public static void main(String[] args) {
            JLabel label = new JLabel(new ImageIcon(new MissingIcon().getImage()));

            JFrame frame = new JFrame();
            frame.add(label);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(200, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private final int iconHeight;

        private final int iconWidth;

        private transient BufferedImage bufferedImage;

        /**
         * Default: 16 x 16
         */
        private MissingIcon() {
            this(16, 16);
        }

        private MissingIcon(final int width, final int height) {
            super();

            this.iconWidth = width;
            this.iconHeight = height;

            setImage(null);
            //            setImage(new ImageIcon(new byte[]{0, 0}).getImage());
        }

        @Override
        public int getIconHeight() {
            return this.iconHeight;
        }

        @Override
        public int getIconWidth() {
            return this.iconWidth;
        }

        @Override
        public Image getImage() {
            if (this.bufferedImage == null) {
                this.bufferedImage = new BufferedImage(getIconWidth() + 1, getIconHeight() + 1, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = this.bufferedImage.getGraphics();
                paintIcon(null, graphics, 0, 0);
                graphics.dispose();
            }

            return this.bufferedImage;
        }

        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.getRenderingHintsQuality());

            g.setColor(Color.RED);
            g.translate(x, y);
            g.drawLine(0, 0, getIconWidth(), getIconHeight());
            g.drawLine(getIconWidth(), 0, 0, getIconHeight());
            g.drawRect(0, 0, getIconWidth(), getIconHeight());
            g.translate(-x, -y);
        }

        @Override
        protected void loadImage(final Image image) {
            // Empty
        }
    }

    /**
     * Icon für einen Pfeil.
     *
     * @author Thomas Freese
     */
    private static final class TriangleIcon extends ImageIcon {
        @Serial
        private static final long serialVersionUID = 6491045895051309036L;

        public static void main(String[] args) {
            JLabel label = new JLabel(new ImageIcon(new TriangleIcon(SwingConstants.NORTH).getImage()));

            JFrame frame = new JFrame();
            frame.add(label);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(200, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private final int direction;

        private final Color foreground;

        private final int iconHeight;

        private final int iconWidth;

        private transient BufferedImage bufferedImage;

        /**
         * Defaults: Width = 16, Height = 16, ForeGround = Black
         *
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         */
        private TriangleIcon(final int direction) {
            this(16, 16, direction, Color.BLACK);
        }

        /**
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         */
        private TriangleIcon(final int width, final int height, final int direction, final Color foreground) {
            super();

            this.iconWidth = width;
            this.iconHeight = height;
            this.direction = direction;
            this.foreground = foreground;

            if ((this.direction != SwingConstants.NORTH) && (this.direction != SwingConstants.SOUTH) && (this.direction != SwingConstants.EAST) && (this.direction != SwingConstants.WEST)) {
                throw new IllegalArgumentException("Only SwingConstants.NORTH, SOUTH, EAST, WEST supported !");
            }

            setImage(null);
            //            setImage(new ImageIcon(new byte[]{0, 0}).getImage());
        }

        @Override
        public int getIconHeight() {
            return this.iconHeight;
        }

        @Override
        public int getIconWidth() {
            return this.iconWidth;
        }

        @Override
        public Image getImage() {
            if (this.bufferedImage == null) {
                this.bufferedImage = new BufferedImage(getIconWidth() + 1, getIconHeight() + 1, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = this.bufferedImage.getGraphics();
                paintIcon(null, graphics, 0, 0);
                graphics.dispose();
            }

            return this.bufferedImage;
        }

        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.getRenderingHintsQuality());
            g2d.setColor(this.foreground);
            int centerX = getIconWidth() / 2;
            int centerY = getIconHeight() / 2;
            int[] xPoints;
            int[] yPoints;

            if (this.direction == SwingConstants.NORTH) {
                xPoints = new int[]{x, x + centerX, x + (centerX * 2)};
                yPoints = new int[]{y + (centerY * 2), y, y + (centerY * 2)};
            }
            else if (this.direction == SwingConstants.SOUTH) {
                xPoints = new int[]{x, x + centerX, x + (centerX * 2)};
                yPoints = new int[]{y, y + (centerY * 2), y};
            }
            else if (this.direction == SwingConstants.WEST) {
                xPoints = new int[]{x + (centerX * 2), x, x + (centerX * 2)};
                yPoints = new int[]{y, y + centerY, y + (centerY * 2)};
            }
            else if (this.direction == SwingConstants.EAST) {
                xPoints = new int[]{x, x + (centerX * 2), x, x};
                yPoints = new int[]{y, y + centerY, y + (centerY * 2)};
            }
            else {
                throw new IllegalStateException();
            }

            g2d.fillPolygon(xPoints, yPoints, 3);
        }

        @Override
        protected void loadImage(final Image image) {
            // Empty
        }
    }

    /**
     * Erzeugt ein leeres Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon ohne Inhalt
     */
    public static ImageIcon createEmptyIcon() {
        return new EmptyIcon();
    }

    public static ImageIcon createEmptyIcon(final int width, final int height) {
        return new EmptyIcon(width, height);
    }

    /**
     * Erzeugt ein Missing-Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon (Roter Kasten mit rotem X)
     */
    public static ImageIcon createMissingIcon() {
        return new MissingIcon();
    }

    public static ImageIcon createMissingIcon(final int width, final int height) {
        return new MissingIcon(width, height);
    }

    /**
     * Liefert ein TriangleIcon mit einer Größe von 16x16 Pixel und schwarzem Vordergrund.
     *
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     */
    public static ImageIcon createTriangleIcon(final int direction) {
        return new TriangleIcon(direction);
    }

    /**
     * Liefert ein TriangleIcon.
     *
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     */
    public static ImageIcon createTriangleIcon(final int width, final int height, final int direction, final Color foreground) {
        return new TriangleIcon(width, height, direction, foreground);
    }

    public static BufferedImage decode(final byte[] bytes) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return decode(inputStream);
        }
    }

    /**
     * Stream is not closed.
     */
    public static BufferedImage decode(final InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    /**
     * Encode the image in a specific format.
     */
    public static byte[] encode(final Image image, final ImageFormat format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeImage(image, format, baos);

            baos.flush();

            return baos.toByteArray();
        }
    }

    public static RenderingHints getRenderingHintsQuality() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return hints;
    }

    /**
     * Liefert true, wenn das {@link Image} transparente Pixel enthält.
     */
    public static boolean hasAlpha(final Image image) {
        if (image instanceof BufferedImage bImage) {
            return bImage.getColorModel().hasAlpha();
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

        try {
            pg.grabPixels();
        }
        catch (InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }

        ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }

    /**
     * Vereint zwei Bilder in einem. dabei wird das zweite über das erste Bild gelegt.
     */
    public static BufferedImage merge(final Image image, final Image overlay) {
        int width = Math.max(image.getWidth(null), overlay.getWidth(null));
        int height = Math.max(image.getHeight(null), overlay.getHeight(null));

        BufferedImage merged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        RenderingHints hints = getRenderingHintsQuality();

        Graphics2D graphics = merged.createGraphics();
        graphics.setRenderingHints(hints);
        graphics.drawImage(image, 0, 0, null);
        graphics.drawImage(overlay, 0, 0, null);
        graphics.dispose();

        return merged;
    }

    /**
     * Vereint zwei Bilder in einem, dabei wird das zweite über das erste Bild gelegt.
     */
    public static BufferedImage merge(final ImageIcon image, final ImageIcon overlay) {
        return merge(image.getImage(), overlay.getImage());
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     */
    public static BufferedImage scaleImage(final Image src, final int width, final int height) {
        Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return toBufferedImage(scaled);
    }

    /**
     * Skaliert das Bild auf die neuen Seitenverhältnisse.
     */
    public static BufferedImage scaleImageByRatio(final Image src, final double ratioWidth, final double ratioHeight) {
        BufferedImage bufferedImage = toBufferedImage(src);

        AffineTransform tx = new AffineTransform();
        tx.scale(ratioWidth, ratioHeight);

        // tx.shear(shiftX, shiftY);
        // tx.translate(x, y);
        // tx.rotate(radians, origin.getWidth()/2, origin.getHeight()/2);

        RenderingHints hints = getRenderingHintsQuality();

        AffineTransformOp op = new AffineTransformOp(tx, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Skaliert das Bild unter Beibehaltung des Seitenverhältnisses bis auf die maximale angegebene Höhe oder Breite.
     */
    public static BufferedImage scaleImageKeepRatio(final Image src, final int maxWidth, final int maxHeight) {
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
     * Liefert das Schwarzweissbild.
     */
    public static BufferedImage toBlackWhiteImage(final BufferedImage image) {
        RenderingHints hints = getRenderingHintsQuality();

        BufferedImageOp op = new BlackWhiteOp(hints, 0);

        return op.filter(image, null);
    }

    /**
     * Liefert das Schwarzweissbild.
     */
    public static BufferedImage toBlackWhiteImage(final Image image) {
        BufferedImage bufferedImage = toBufferedImage(image);

        return toBlackWhiteImage(bufferedImage);
    }

    /**
     * Kopiert ein Icon in eine Image-Kopie.
     */
    public static BufferedImage toBufferedImage(final Icon icon) {
        if (icon instanceof ImageIcon imageIcon && imageIcon.getImage() instanceof BufferedImage bi) {
            return bi;
        }

        BufferedImage returnImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = returnImage.getGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        return returnImage;
    }

    /**
     * Konvertiert ein {@link Image} in ein {@link BufferedImage}.
     */
    public static BufferedImage toBufferedImage(final Image image) {
        if (image instanceof BufferedImage bi) {
            return bi;
        }

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        RenderingHints hints = getRenderingHintsQuality();

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHints(hints);
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
    }

    /**
     * Liefert das Kanten Bild.
     */
    public static BufferedImage toEdgeImage(final Image image) {
        BufferedImage bufferedImage = toBufferedImage(image);

        // Sobel Operator, horizontal & vertikal
        float[] matrix = {0.0F, -1.0F, 0.0F, -1.0F, 4.0F, -1.0F, 0.0F, -1.0F, 0.0F};

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
        RenderingHints hints = getRenderingHintsQuality();

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Graut das Icon aus.
     */
    public static ImageIcon toGrayIcon(final Icon icon) {
        BufferedImage bufferedImage = toBufferedImage(icon);
        Image result = toGrayImage(bufferedImage);

        return new ImageIcon(result);
    }

    /**
     * Graut das Icon aus.
     */
    public static ImageIcon toGrayIcon(final ImageIcon imageIcon, final int percent) {
        Image image = toGrayImage(imageIcon.getImage(), percent);

        return new ImageIcon(image);
    }

    /**
     * Erzeugt ein Graustufenbild.
     */
    public static BufferedImage toGrayImage(final BufferedImage bufferedImage) {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        RenderingHints hints = getRenderingHintsQuality();

        ColorConvertOp op = new ColorConvertOp(colorSpace, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Graut das Bild aus.
     *
     * @param percent int, 0-100%
     */
    public static Image toGrayImage(final Image image, final int percent) {
        ImageFilter filter = new GrayFilter(true, percent);
        ImageProducer prod = new FilteredImageSource(image.getSource(), filter);

        return Toolkit.getDefaultToolkit().createImage(prod);
    }

    /**
     * Liefert das geschärfte Bild.
     */
    public static BufferedImage toSharpenImage(final BufferedImage image) {
        float[] matrix = {0.0F, -1.0F, 0.0F, -1.0F, 5.0F, -1.0F, 0.0F, -1.0F, 0.0F};

        // Kantenglättung
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
        RenderingHints hints = getRenderingHintsQuality();

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(image, null);
    }

    /**
     * Liefert das geschärfte Bild.
     */
    public static BufferedImage toSharpenImage(final Image image) {
        BufferedImage bufferedImage = toBufferedImage(image);

        return toSharpenImage(bufferedImage);
    }

    /**
     * Stream is not closed.
     */
    public static void writeImage(final BufferedImage image, final ImageFormat format, final OutputStream outputStream) throws IOException {
        ImageIO.write(image, format.name(), outputStream);
    }

    /**
     * Stream is not closed.
     */
    public static void writeImage(final Image image, final ImageFormat format, final OutputStream outputStream) throws IOException {
        BufferedImage bufferedImage = toBufferedImage(image);

        writeImage(bufferedImage, format, outputStream);
    }

    private ImageUtils() {
        super();
    }
}
