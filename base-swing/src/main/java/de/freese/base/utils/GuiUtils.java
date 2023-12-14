package de.freese.base.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Serial;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jakarta.activation.DataSource;

import de.freese.base.core.image.ImageFormat;

/**
 * @author Thomas Freese
 */
public final class GuiUtils {
    /**
     * @author Thomas Freese
     */
    private static final class ToolBarSeparator extends JPanel {
        @Serial
        private static final long serialVersionUID = -7069549788210052499L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(16, 1);
        }

        @Override
        public void paint(final Graphics g) {
            // Empty
        }
    }

    /**
     * Passt die Grösse des übergebenen Fensters der aktuellen Bildschirmauflösung an.<br>
     * <br>
     * Solange das Fenster vollständig dargestellt werden kann, werden keine Änderungen vorgenommen. Ansonsten wird das Fenster entsprechend der aktuellen
     * Bildschirmauflösung verkleinert.<br>
     */
    public static void adjustFrame(final JFrame frame) {
        final Dimension screenSize = GuiUtils.getScreenSize();

        if ((frame.getWidth() > screenSize.width) || (frame.getHeight() > screenSize.height)) {
            frame.setSize(screenSize);
        }
    }

    /**
     * Liefert die Breite eines Strings in der übergebenen Komponente.
     */
    public static int calcTextWidth(final JComponent component, final String text) {
        return component.getFontMetrics(component.getFont()).stringWidth(text);
    }

    /**
     * Erzeugt einen PNG-Screenshot als {@link DataSource}.
     */
    public static DataSource createScreenShot() throws Exception {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int) dimension.getWidth();
        final int height = (int) dimension.getHeight();

        final DataSource dataSource = GuiUtils.createScreenShot(0, 0, width, height);

        if (dataSource instanceof ByteArrayDataSource b) {
            final String fileName = "screenshot_" + System.currentTimeMillis() + ".png";

            b.setName(fileName);
        }

        return dataSource;
    }

    /**
     * Erzeugt einen PNG-Screenshot einer {@link Component} als {@link DataSource}.
     */
    public static DataSource createScreenShot(final Component c) throws Exception {
        Objects.requireNonNull(c, "component required");

        final int x = c.getX();
        final int y = c.getY();
        final int width = c.getWidth();
        final int height = c.getHeight();

        final DataSource dataSource = GuiUtils.createScreenShot(x, y, width, height);

        if (dataSource instanceof ByteArrayDataSource b) {
            String fileName = "screenshot";
            fileName += c.getName() != null ? "_" + c.getName() : "";
            fileName += "_" + System.currentTimeMillis();
            fileName += ".png";

            b.setName(fileName);
        }

        return dataSource;
    }

    /**
     * Erzeugt einen PNG-Screenshot des Koordinatenbereichs als {@link DataSource}.
     */
    public static DataSource createScreenShot(final int x, final int y, final int width, final int height) throws Exception {
        final BufferedImage shot = new Robot().createScreenCapture(new Rectangle(x, y, width, height));

        ByteArrayDataSource dataSource = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageUtils.writeImage(shot, ImageFormat.PNG, baos);

            baos.flush();

            dataSource = new ByteArrayDataSource(baos.toByteArray(), ByteArrayDataSource.MIMETYPE_IMAGE_PNG);
        }

        return dataSource;
    }

    /**
     * Findet in einer Component eine andere Component mit der angegebene Klasse.<br>
     * Wird nichts gefunden, kommt null zurück.
     */
    public static Component find(final Component comp, final Class<?> clazz) {
        Component found = null;

        if (clazz.isInstance(comp)) {
            found = comp;
        }
        else if (comp instanceof Container c) {
            for (int i = 0; i < c.getComponentCount(); i++) {
                found = GuiUtils.find(c.getComponent(i), clazz);

                if (found != null) {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Findet in einer Component eine andere Component mit dem angegebenen Namen.<br>
     * Wird nichts gefunden, kommt null zurück.<br>
     */
    public static Component find(final Component comp, final String name) {
        final String compName = comp.getName();

        Component found = null;

        if ((compName != null) && compName.equals(name)) {
            found = comp;
        }
        else if (comp instanceof Container c) {
            for (int i = 0; i < c.getComponentCount(); i++) {
                found = GuiUtils.find(c.getComponent(i), name);

                if (found != null) {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Gibt das aktuell fokussierte Frame zurück, oder das erste Frame, welches gefunden wird.
     */
    public static Frame getActiveFrame() {
        final Frame[] frames = Frame.getFrames();
        Frame activeFrame = null;

        for (Frame frame : frames) {
            if (frame.hasFocus()) {
                activeFrame = frame;
            }
        }

        // Fallback: Erstes Frame nehmen
        if ((activeFrame == null) && (frames.length > 0)) {
            activeFrame = frames[0];
        }

        return activeFrame;
    }

    /**
     * Gibt die Root-Komponente einer Komponente zurück.
     */
    public static Component getRoot(final Component component) {
        Component c = component;

        while (c.getParent() != null) {
            c = c.getParent();
        }

        return c;
    }

    /**
     * Gibt die {@link Dimension} des Bildschirms in Pixel zurück.<br>
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Liefert den selbstgebauten ToolBar-Separator.
     */
    public static JPanel getToolBarSeparator() {
        return new ToolBarSeparator();
    }

    private GuiUtils() {
        super();
    }
}
