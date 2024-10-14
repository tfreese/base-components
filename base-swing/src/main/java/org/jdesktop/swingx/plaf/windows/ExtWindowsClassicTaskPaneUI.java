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
 * Überschreibt den ContentPaneBorder der SwingX UI der TaskPane, damit der Content nur 2 Pixel Abstand hat und nicht 10.
 *
 * @author Thomas Freese
 */
public class ExtWindowsClassicTaskPaneUI extends WindowsClassicTaskPaneUI {
    protected static final int TITLE_ICON_SIZE = 16;

    public static ComponentUI createUI(final JComponent c) {
        return new ExtWindowsClassicTaskPaneUI();
    }

    /**
     * @author Thomas Freese
     */
    private final class ExtPaneBorder extends ClassicPaneBorder {
        @Override
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
            super.paintBorder(c, g, x, y, width, height);

            paintTitleActions(g, x, y, width, height);
        }

        @Override
        protected boolean isMouseOverBorder() {
            return true;
        }

        @Override
        protected void paintTitleBackground(final JXTaskPane group, final Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            final Paint storedPaint = g2.getPaint();

            if (group.isSpecial()) {
                g.setColor(this.specialTitleBackground);
            }
            else {
                g.setColor(this.titleBackgroundGradientStart);

                g2.setPaint(new GradientPaint(0, 0, this.titleBackgroundGradientStart, 0, getTitleHeight(null), this.titleBackgroundGradientEnd));
            }

            g.fillRect(0, 0, group.getWidth(), getTitleHeight(null) - 1);
            g2.setPaint(storedPaint);
        }

        private void paintTitleActions(final Graphics g, final int x, final int y, final int width, final int height) {
            // Weitere Buttons malen
            for (JButton button : ExtWindowsClassicTaskPaneUI.this.titleButtons) {
                final Rectangle rectangle = getRectangleFor(button);

                if (rectangle == null) {
                    continue;
                }

                if (button.getIcon() == null) {
                    button.setIcon(ImageUtils.createMissingIcon());
                }

                Icon icon = button.getIcon();

                if (ExtWindowsClassicTaskPaneUI.this.group.isCollapsed() || !button.isEnabled()) {
                    // Icon ausgrauen, wenn zugeklappt oder Button disabled
                    icon = ImageUtils.toGrayIcon(icon);
                }

                icon.paintIcon(null, g, rectangle.x, rectangle.y);
            }

            if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed()) {
                // Herausfinden, ob unter Courser ein Button liegt, dann Tooltip malen.
                // Alternativ im ToggleListener.mouseMoved malen lassen.
                final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                final Point point = new Point(pointerInfo.getLocation());
                SwingUtilities.convertPointFromScreen(point, ExtWindowsClassicTaskPaneUI.this.group);

                final JButton button = getButtonFor(point.x, point.y);

                if (button != null) {
                    paintToolTip(button);
                }
            }
        }
    }

    /**
     * Listener für den Titel.
     *
     * @author Thomas Freese
     */
    private final class ToggleListener extends MouseInputAdapter {
        @Override
        public void mouseEntered(final MouseEvent event) {
            if (isInBorder(event)) {
                event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            else {
                ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();

                if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed()) {
                    final JButton button = getButtonFor(event.getX(), event.getY());

                    if (button != null) {
                        final MouseEvent evt = new MouseEvent(button,
                                MouseEvent.MOUSE_ENTERED,
                                event.getWhen(),
                                event.getModifiersEx(),
                                event.getX(),
                                event.getY(),
                                event.getClickCount(),
                                event.isPopupTrigger());
                        button.dispatchEvent(evt);
                    }
                }
            }
        }

        @Override
        public void mouseExited(final MouseEvent event) {
            event.getComponent().setCursor(null);
            ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
            ExtWindowsClassicTaskPaneUI.this.group.repaint();
        }

        @Override
        public void mouseMoved(final MouseEvent event) {
            if (isInBorder(event)) {
                event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ExtWindowsClassicTaskPaneUI.this.mouseOver = true;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();
            }
            else {
                event.getComponent().setCursor(null);
                ExtWindowsClassicTaskPaneUI.this.mouseOver = false;
                ExtWindowsClassicTaskPaneUI.this.group.repaint();

                // if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed()) {
                //     final JButton button = getButtonFor(e.getX(), e.getY());
                //
                //     if (button != null) {
                //         // Für den Tooltip
                //         // TODO Funktioniert so nicht, da der Button in keiner Komponenten-Hierarchie hängt, sondern nur gerendert wird.
                //         // final MouseEvent evt =
                //         // new MouseEvent(
                //         // button, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(),
                //         // event.getModifiers(),
                //         // TITLE_ICON_SIZE - 1, TITLE_ICON_SIZE - 1, e.getClickCount(),
                //         // event.isPopupTrigger()
                //         // );
                //         //
                //         // button.dispatchEvent(evt);
                //         //
                //         // paintToolTip(button);
                //     }
                // }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent event) {
            if (isInBorder(event)) {
                ExtWindowsClassicTaskPaneUI.this.group.setCollapsed(!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed());
            }
            else if (!ExtWindowsClassicTaskPaneUI.this.group.isCollapsed()) {
                final JButton button = getButtonFor(event.getX(), event.getY());

                if (button != null && !event.isPopupTrigger()) {
                    button.doClick();
                }
            }
        }
    }

    /**
     *
     */
    private final PropertyChangeListener buttonEnabledPropertyChangeListener;
    /**
     * Liste aller Buttons TitelLeiste
     */
    private final List<JButton> titleButtons = new ArrayList<>();
    /**
     * Liste aller Buttons und Separators der TitelLeiste
     */
    private final List<Object> titleObjects = new ArrayList<>();

    /**
     * Creates a new {@link ExtWindowsClassicTaskPaneUI} object.
     */
    public ExtWindowsClassicTaskPaneUI() {
        super();

        this.buttonEnabledPropertyChangeListener = event -> this.group.repaint();
    }

    /**
     * Hinzufügen eines Separators in den Title.
     */
    public void addSeparator() {
        this.titleObjects.add(new Dimension(TITLE_ICON_SIZE, TITLE_ICON_SIZE));
    }

    /**
     * Hinzufügen eines Separators in den Title.
     *
     * @param dimension {@link Dimension}
     */
    public void addSeparator(final Dimension dimension) {
        this.titleObjects.add(dimension);
    }

    /**
     * Hinzufügen eines Buttons in den Title.
     *
     * @param button {@link JButton}
     */
    public void addTitleButton(final JButton button) {
        button.setBounds(0, 0, TITLE_ICON_SIZE, TITLE_ICON_SIZE);

        this.titleButtons.add(button);
        this.titleObjects.add(button);

        button.addPropertyChangeListener("enabled", this.buttonEnabledPropertyChangeListener);
    }

    @Override
    protected Border createContentPaneBorder() {
        final Color borderColor = UIManager.getColor("TaskPane.borderColor");

        return new CompoundBorder(new ContentPaneBorder(borderColor), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    @Override
    protected MouseInputListener createMouseInputListener() {
        return new ToggleListener();
    }

    @Override
    protected Border createPaneBorder() {
        return new ExtPaneBorder();
    }

    @Override
    protected int getTitleHeight(final Component c) {
        // return super.getTitleHeight(c);
        return this.titleHeight;
    }

    @Override
    protected boolean isInBorder(final MouseEvent me) {
        boolean result = super.isInBorder(me);

        // Über eigene TitleButtons nicht den Courser ändern
        if (result) {
            // Prüfen, ob auf einem TitleButton geklickt wurde
            final JButton button = getButtonFor(me.getX(), me.getY());

            if (button != null) {
                result = false;
            }
        }

        return result;
    }

    protected void paintToolTip(final JButton button) {
        if (this.group.isCollapsed()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            final String text = button.getToolTipText();

            if (text != null && !text.isEmpty()) {
                final Rectangle rectangle = getRectangleFor(button);
                final Graphics2D g2 = (Graphics2D) ExtWindowsClassicTaskPaneUI.this.group.getGraphics();

                g2.setFont(UIManager.getFont("ToolTip.font"));

                final FontMetrics fm = g2.getFontMetrics();
                final int RAND = 5; // Abstand vom Rahmen zum Text
                final double textWidth = fm.getStringBounds(text, g2).getWidth() + (2 * RAND); // 2* :
                // Links & Rechts des TaskPanes
                final double maxWidth = ExtWindowsClassicTaskPaneUI.this.group.getBounds().width; // Breite
                final double diff = (textWidth + rectangle.getX()) - maxWidth; // Überstand
                final int useX = (int) ((diff > 0) ? (rectangle.getX() - diff) : rectangle.getX());
                final int useY = (int) rectangle.getMaxY() + 16; // 16 : Höhe des MouseIcons

                // Tooltip Background
                final Rectangle rect = new Rectangle(useX, useY, (int) textWidth, fm.getHeight() + 2);
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

    private JButton getButtonFor(final int x, final int y) {
        for (JButton button : this.titleButtons) {
            final Rectangle rect = getRectangleFor(button);

            if (rect == null) {
                continue;
            }

            if (rect.contains(x, y)) {
                return button;
            }
        }

        return null;
    }

    private Rectangle getRectangleFor(final JButton button) {
        final int index = this.titleObjects.indexOf(button);

        if (index < 0) {
            return null;
        }

        int x = this.group.getWidth() - 40;

        // Breiten der anderen Icons abziehen
        for (int i = 0; i < index; i++) {
            final Object titelObject = this.titleObjects.get(i);

            if (titelObject instanceof Dimension d) {
                x -= d.width;
            }
            else {
                x -= ((JButton) titelObject).getBounds().width; // TITLE_ICON_SIZE;
            }

            x -= 5; // Abstand zwischen Icons
        }

        // Button selbst berücksichtigen
        x -= button.getBounds().width; // TITLE_ICON_SIZE;

        // return new Rectangle(x, 4, TITLE_ICON_SIZE, TITLE_ICON_SIZE);
        return new Rectangle(x, 4, button.getBounds().width, button.getBounds().height);
    }
}
