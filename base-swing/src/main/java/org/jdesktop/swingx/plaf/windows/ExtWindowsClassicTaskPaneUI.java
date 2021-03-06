package org.jdesktop.swingx.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXTaskPane;
import de.freese.base.utils.ImageUtils;

/**
 * Ueberschreibt den ContentPaneBorder der SwingX UI der TaskPane, damit der Content nur 2 Pixel Abstand hat und nicht 10.
 *
 * @author Thomas Freese
 */
public class ExtWindowsClassicTaskPaneUI extends WindowsClassicTaskPaneUI
{
    /**
     * TitledBorder.
     *
     * @author Thomas Freese
     */
    private class ExtPaneBorder extends ClassicPaneBorder
    {
        /**
         * Creates a new {@link ExtPaneBorder} object.
         */
        public ExtPaneBorder()
        {
            super();
        }

        /**
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#isMouseOverBorder()
         */
        @Override
        protected boolean isMouseOverBorder()
        {
            return true;
        }

        /**
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
         */
        @Override
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height)
        {
            super.paintBorder(c, g, x, y, width, height);

            paintTitleActions(g, x, y, width, height);
        }

        /**
         * Malen weiterer TitleButtons.
         *
         * @param g Graphics
         * @param x int
         * @param y int
         * @param width int
         * @param height int
         */
        protected void paintTitleActions(final Graphics g, final int x, final int y, final int width, final int height)
        {
            // Weitere Buttons malen
            for (JButton button : ExtWindowsClassicTaskPaneUI.this.titleButtons)
            {
                Rectangle rectangle = getRectangleFor(button);

                if (button.getIcon() == null)
                {
                    button.setIcon(ImageUtils.createMissingIcon());
                }

                Icon icon = button.getIcon();

                if (ExtWindowsClassicTaskPaneUI.this.group.isCollapsed() || !button.isEnabled())
                {
                    // Icon ausgrauen, wenn zugeklappt oder Button disabled
                    icon = ImageUtils.toGrayIcon(icon);
                }

                icon.paintIcon(null, g, rectangle.x, rectangle.y);
            }

            if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed())
            {
                // Rausfinden ob unter Courser ein Button liegt, dann Tooltip malen
                // Alternativ im ToggleListener.mouseMoved malen lassen
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                Point point = new Point(pointerInfo.getLocation());
                SwingUtilities.convertPointFromScreen(point, ExtWindowsClassicTaskPaneUI.this.group);

                JButton button = getButtonFor(point.x, point.y);

                if (button != null)
                {
                    // TODO ToolTipManager verwenden
                    paintToolTip(button);
                }
            }
        }

        /**
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#paintTitleBackground(org.jdesktop.swingx.JXTaskPane, java.awt.Graphics)
         */
        @Override
        protected void paintTitleBackground(final JXTaskPane group, final Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;
            Paint storedPaint = g2.getPaint();

            if (group.isSpecial())
            {
                g.setColor(this.specialTitleBackground);
            }
            else
            {
                g.setColor(this.titleBackgroundGradientStart);

                g2.setPaint(new GradientPaint(0, 0, this.titleBackgroundGradientStart, 0, getTitleHeight(null), this.titleBackgroundGradientEnd));
            }

            g.fillRect(0, 0, group.getWidth(), getTitleHeight(null) - 1);
            g2.setPaint(storedPaint);
        }
    }

    /**
     * Listener fuer den Titel.
     *
     * @author Thomas Freese
     */
    private class ToggleListener extends MouseInputAdapter
    {
        /**
         * @see javax.swing.event.MouseInputAdapter#mouseEntered(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseEntered(final MouseEvent e)
        {
            if (isInBorder(e))
            {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            else
            {
                ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();

                if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed())
                {
                    JButton button = getButtonFor(e.getX(), e.getY());

                    if (button != null)
                    {
                        MouseEvent evt = new MouseEvent(button, MouseEvent.MOUSE_ENTERED, e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
                                e.getClickCount(), e.isPopupTrigger());
                        button.dispatchEvent(evt);
                    }
                }
            }
        }

        /**
         * @see javax.swing.event.MouseInputAdapter#mouseExited(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseExited(final MouseEvent e)
        {
            e.getComponent().setCursor(null);
            ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
            ExtWindowsClassicTaskPaneUI.this.group.repaint();
        }

        /**
         * @see javax.swing.event.MouseInputAdapter#mouseMoved(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseMoved(final MouseEvent e)
        {
            if (isInBorder(e))
            {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ExtWindowsClassicTaskPaneUI.this.mouseOver = true;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();
            }
            else
            {
                e.getComponent().setCursor(null);
                ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();

                if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed())
                {
                    JButton button = getButtonFor(e.getX(), e.getY());

                    if (button != null)
                    {
                        // Fuer den Tooltip
                        // TODO Funktioniert so nicht, da der Button in keiner Komponentenhierachie
                        // haengt, sondern nur gerendert wird.
                        // MouseEvent evt =
                        // new MouseEvent(
                        // button, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                        // e.getModifiers(),
                        // TITLE_ICON_SIZE - 1, TITLE_ICON_SIZE - 1, e.getClickCount(),
                        // e.isPopupTrigger()
                        // );
                        //
                        // button.dispatchEvent(evt);
                        //
                        // paintToolTip(button);
                    }
                }
            }
        }

        /**
         * @see javax.swing.event.MouseInputAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(final MouseEvent e)
        {
            if (isInBorder(e))
            {
                ExtWindowsClassicTaskPaneUI.this.group.setCollapsed(!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed());
            }
            else if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed())
            {
                JButton button = getButtonFor(e.getX(), e.getY());

                if ((button != null) && !e.isPopupTrigger())
                {
                    button.doClick();
                }
            }
        }
    }

    /**
     *
     */
    protected static final int TITLE_ICON_SIZE = 16;

    /**
     * Erzeugt eine UI Instanz.
     *
     * @param c {@link JComponent}
     * @return {@link ComponentUI}
     */
    public static ComponentUI createUI(final JComponent c)
    {
        return new ExtWindowsClassicTaskPaneUI();
    }

    /**
     *
     */
    private PropertyChangeListener buttonEnabledPropertyChangeListener;

    /**
     * Liste aller Buttons TitelLeiste
     */
    protected List<JButton> titleButtons = new ArrayList<>();

    /**
     * Liste aller Buttons & Separators der TitelLeiste
     */
    protected List<Object> titleObjects = new ArrayList<>();

    /**
     * Creates a new {@link ExtWindowsClassicTaskPaneUI} object.
     */
    public ExtWindowsClassicTaskPaneUI()
    {
        super();

        this.buttonEnabledPropertyChangeListener = event -> this.group.repaint();
    }

    /**
     * Hinzufuegen eines Separators in den Title.
     */
    public void addSeparator()
    {
        this.titleObjects.add(new Dimension(TITLE_ICON_SIZE, TITLE_ICON_SIZE));
    }

    /**
     * Hinzufuegen eines Separators in den Title.
     *
     * @param dimension {@link Dimension}
     */
    public void addSeparator(final Dimension dimension)
    {
        this.titleObjects.add(dimension);
    }

    /**
     * Hinzufuegen eines Buttons in den Title.
     *
     * @param button {@link JButton}
     */
    public void addTitleButton(final JButton button)
    {
        button.setBounds(0, 0, TITLE_ICON_SIZE, TITLE_ICON_SIZE);

        this.titleButtons.add(button);
        this.titleObjects.add(button);

        button.addPropertyChangeListener("enabled", this.buttonEnabledPropertyChangeListener);
    }

    /**
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#createContentPaneBorder()
     */
    @Override
    protected Border createContentPaneBorder()
    {
        Color borderColor = UIManager.getColor("TaskPane.borderColor");

        return new CompoundBorder(new ContentPaneBorder(borderColor), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    /**
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#createMouseInputListener()
     */
    @Override
    protected MouseInputListener createMouseInputListener()
    {
        return new ToggleListener();
    }

    /**
     * @see org.jdesktop.swingx.plaf.windows.WindowsClassicTaskPaneUI#createPaneBorder()
     */
    @Override
    protected Border createPaneBorder()
    {
        return new ExtPaneBorder();
    }

    /**
     * Liefert den JButton, der untern den Mouse-Koordinaten liegt, oder null.
     *
     * @param x int
     * @param y int
     * @return {@link JButton}
     */
    private JButton getButtonFor(final int x, final int y)
    {
        for (JButton button : this.titleButtons)
        {
            Rectangle rect = getRectangleFor(button);

            if (rect.contains(x, y))
            {
                return button;
            }
        }

        return null;
    }

    /**
     * Liefert den Pixel-Bereich eines Buttons.
     *
     * @param button {@link JButton}
     * @return {@link Rectangle}
     */
    private Rectangle getRectangleFor(final JButton button)
    {
        int index = this.titleObjects.indexOf(button);

        if (index < 0)
        {
            return null;
        }

        int x = this.group.getWidth() - 40;

        // Breiten der anderen Icons abziehen
        for (int i = 0; i < index; i++)
        {
            Object titelObject = this.titleObjects.get(i);

            if (titelObject instanceof Dimension)
            {
                x -= ((Dimension) titelObject).width;
            }
            else
            {
                x -= ((JButton) titelObject).getBounds().width; // TITLE_ICON_SIZE;
            }

            x -= 5; // Abstand zwischen Icons
        }

        // Button selbst beruecksichtigen
        x -= button.getBounds().width; // TITLE_ICON_SIZE;

        // Rectangle rect = new Rectangle(x, 4, TITLE_ICON_SIZE, TITLE_ICON_SIZE);
        Rectangle rect = new Rectangle(x, 4, button.getBounds().width, button.getBounds().height);

        return rect;
    }

    /**
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#getTitleHeight(java.awt.Component)
     */
    @Override
    protected int getTitleHeight(final Component c)
    {
        // return super.getTitleHeight(c);
        return this.titleHeight;
    }

    /**
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#isInBorder(java.awt.event.MouseEvent)
     */
    @Override
    protected boolean isInBorder(final MouseEvent me)
    {
        boolean result = super.isInBorder(me);

        // Ueber eigene TitleButtons nicht den Courser aendern
        if (result)
        {
            // Pruefen, ob auf einem TitleButton geklickt wurde
            JButton button = getButtonFor(me.getX(), me.getY());

            if (button != null)
            {
                result = false;
            }
        }

        return result;
    }

    /**
     * Malt den Tooltip des Buttons, wenn vorhanden.
     *
     * @param button {@link JButton}
     */
    protected void paintToolTip(final JButton button)
    {
        if (this.group.isCollapsed())
        {
            return;
        }

        // TODO ToolTipManager verwenden
        SwingUtilities.invokeLater(() -> {
            String text = button.getToolTipText();

            if ((text != null) && (text.length() > 0))
            {
                Rectangle rectangle = getRectangleFor(button);
                Graphics2D g2 = (Graphics2D) ExtWindowsClassicTaskPaneUI.this.group.getGraphics();

                g2.setFont(UIManager.getFont("ToolTip.font"));

                FontMetrics fm = g2.getFontMetrics();
                final int RAND = 5; // Abstand vom Rahmen zum Text
                double textWidth = fm.getStringBounds(text, g2).getWidth() + (2 * RAND); // 2* :
                // Links
                // &
                // Rechts
                double maxWidth = ExtWindowsClassicTaskPaneUI.this.group.getBounds().width; // Breite
                // des
                // TaskPanes
                double diff = (textWidth + rectangle.getX()) - maxWidth; // ueberstand
                int useX = (int) ((diff > 0) ? (rectangle.getX() - diff) : rectangle.getX());
                int useY = (int) rectangle.getMaxY() + 16; // 16 : Hoehe des MouseIcons

                // Tooltip Background
                Rectangle rect = new Rectangle(useX, useY, (int) textWidth, fm.getHeight() + 2);
                g2.setColor(UIManager.getColor("ToolTip.background"));
                g2.fill(rect);

                // Tooltip border
                g2.setColor(UIManager.getColor("ToolTip.foregroundInactive"));
                g2.draw(rect);

                // Text
                g2.setColor(UIManager.getColor("ToolTip.foreground"));
                g2.drawString(text, useX + RAND, (useY + fm.getHeight()) - 2);
            }
        });
    }
}
