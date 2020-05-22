package de.freese.base.utils;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import javax.activation.DataSource;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import de.freese.base.core.image.ImageFormat;

/**
 * Nuetzliches Allerlei fuer die GUI.
 *
 * @author Thomas Freese
 */
public final class GuiUtils
{
    /**
     * Selbstgebauter ToolBar Separator.<br>
     * Hat einen kleinen Strich und eine graue Flaeche.
     *
     * @author Thomas Freese
     */
    private static class ToolBarSeparator extends JPanel
    {
        /**
         *
         */
        private static final long serialVersionUID = -7069549788210052499L;

        /**
         * @see javax.swing.JComponent#getPreferredSize()
         */
        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(16, 1);
        }

        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public void paint(final Graphics g)
        {
            // nichts malen
        }
    }

    /**
     * Passt die Groesse des uebergebenen Fensters der aktuellen Bildschirmaufloesung an.<br>
     * <br>
     * Solange das Fenster vollstaendig dargestellt werden kann, werden keine Aenderungen vorgenommen. Ansonsten wird das Fenster entsprechend der aktuellen
     * Bildschirmaufloesung verkleinert.<br>
     *
     * @param frame {@link JFrame}
     */
    public static void adjustFrame(final JFrame frame)
    {
        Dimension screenSize = GuiUtils.getScreenSize();

        if ((frame.getWidth() > screenSize.width) || (frame.getHeight() > screenSize.height))
        {
            frame.setSize(screenSize);
        }
    }

    /**
     * Liefert die Breite eines Strings in der uebergebenen Komponente.
     *
     * @param component {@link JComponent}
     * @param text {@link String}
     * @return int
     */
    public static int calcTextWidth(final JComponent component, final String text)
    {
        return component.getFontMetrics(component.getFont()).stringWidth(text);
    }

    /**
     * Erzeugt einen PNG-Screenschot als {@link DataSource}.
     *
     * @return {@link DataSource}
     * @throws AWTException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static DataSource createScreenShot() throws AWTException, IOException
    {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();

        DataSource dataSource = GuiUtils.createScreenShot(0, 0, width, height);

        if (dataSource instanceof ByteArrayDataSource)
        {
            String fileName = "screenshot_" + System.currentTimeMillis() + ".png";

            ((ByteArrayDataSource) dataSource).setName(fileName);
        }

        return dataSource;
    }

    /**
     * Erzeugt einen PNG-Screenschot einer {@link Component} als {@link DataSource}.
     *
     * @param c {@link Component}
     * @return String der Dateiname des ScreenShots
     * @throws AWTException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static DataSource createScreenShot(final Component c) throws AWTException, IOException
    {
        Objects.requireNonNull(c, "component required");

        int x = c.getX();
        int y = c.getY();
        int width = c.getWidth();
        int height = c.getHeight();

        DataSource dataSource = GuiUtils.createScreenShot(x, y, width, height);

        if (dataSource instanceof ByteArrayDataSource)
        {
            String fileName = "screenshot";
            fileName += c.getName() != null ? "_" + c.getName() : "";
            fileName += "_" + System.currentTimeMillis();
            fileName += ".png";

            ((ByteArrayDataSource) dataSource).setName(fileName);
        }

        return dataSource;
    }

    /**
     * Erzeugt einen PNG-Screenschot des Koordinatenbereichs als {@link DataSource}.
     *
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     * @return {@link DataSource}
     * @throws AWTException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static DataSource createScreenShot(final int x, final int y, final int width, final int height) throws AWTException, IOException
    {
        BufferedImage shot = new Robot().createScreenCapture(new Rectangle(x, y, width, height));

        ByteArrayDataSource dataSource = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageUtils.writeImage(shot, ImageFormat.png, baos);

            baos.flush();

            dataSource = new ByteArrayDataSource(baos.toByteArray(), ByteArrayDataSource.MIMETYPE_IMAGE_PNG);
        }

        return dataSource;
    }

    /**
     * Findet in einer Componente eine andere Component mit der angegebene Klasse.<br>
     * Wird nichts gefunden, kommt null zurueck.
     *
     * @param comp {@link Component}
     * @param clazz Class
     * @return {@link Component}
     */
    public static Component find(final Component comp, final Class<?> clazz)
    {
        Component found = null;

        if (clazz.isInstance(comp))
        {
            found = comp;
        }
        else if (comp instanceof Container)
        {
            for (int i = 0; i < ((Container) comp).getComponentCount(); i++)
            {
                found = GuiUtils.find(((Container) comp).getComponent(i), clazz);

                if (found != null)
                {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Findet in einer Component eine andere Component mit dem angegebenen Namen.<br>
     * Wird nichts gefunden, kommt null zurueck.<br>
     *
     * @param comp {@link Component}
     * @param name String
     * @return {@link Component}
     */
    public static Component find(final Component comp, final String name)
    {
        String compName = comp.getName();

        Component found = null;

        if ((compName != null) && compName.equals(name))
        {
            found = comp;
        }
        else if (comp instanceof Container)
        {
            for (int i = 0; i < ((Container) comp).getComponentCount(); i++)
            {
                found = GuiUtils.find(((Container) comp).getComponent(i), name);

                if (found != null)
                {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Gibt das aktuell fokussierte Frame zurück, oder das erste Frame, welches gefunden wird.
     *
     * @return {@link Frame}
     */
    public static Frame getActiveFrame()
    {
        Frame[] frames = Frame.getFrames();
        Frame activeFrame = null;

        for (Frame frame : frames)
        {
            if (frame.hasFocus())
            {
                activeFrame = frame;
            }
        }

        // Fallback: Erstes Frame nehmen
        if ((activeFrame == null) && (frames.length > 0))
        {
            activeFrame = frames[0];
        }

        return activeFrame;
    }

    /**
     * Liefert default GridBagConstraints mit fill=BOTH, anchor=CENTER und Insets mit (5, 5, 5, 5).
     *
     * @param col int, siehe auch Konstanten in GridBagConstraints
     * @param row int, siehe auch Konstanten in GridBagConstraints
     * @return {@link GridBagConstraints}
     */
    public static GridBagConstraints getGBC(final int col, final int row)
    {
        return GuiUtils.getGBC(col, row, 1, 1);
    }

    /**
     * Liefert default GridBagConstraints mit fill=BOTH, anchor=CENTER und Insets mit (5, 5, 5, 5).
     *
     * @param col int, siehe auch Konstanten in GridBagConstraints
     * @param row int, siehe auch Konstanten in GridBagConstraints
     * @param colWidth int, siehe auch Konstanten in GridBagConstraints
     * @param rowWidth int, siehe auch Konstanten in GridBagConstraints
     * @return {@link GridBagConstraints}
     */
    public static GridBagConstraints getGBC(final int col, final int row, final int colWidth, final int rowWidth)
    {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = col;
        constraints.gridy = row;
        constraints.gridwidth = colWidth;
        constraints.gridheight = rowWidth;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        return constraints;
    }

    /**
     * Gibt die Root-Komponente einer Komponente zurueck.
     *
     * @param component {@link Component}
     * @return {@link Component}
     */
    public static Component getRoot(final Component component)
    {
        Component m_component = component;

        while (m_component.getParent() != null)
        {
            m_component = m_component.getParent();
        }

        return m_component;
    }

    /**
     * Gibt die {@link Dimension} des Bildschirms in Pixel zurueck.<br>
     *
     * @return {@link Dimension}
     * @see Toolkit
     */
    public static final Dimension getScreenSize()
    {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Liefert den selbstgebauten ToolBar Separator.
     *
     * @return {@link JPanel}
     */
    public static JPanel getToolBarSeparator()
    {
        return new ToolBarSeparator();
    }
}
