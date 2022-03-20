// Created: 15.04.2008
package de.freese.base.reports.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;

/**
 * Interface eines LayoutElementes des Berichtswesens.
 *
 * @author Thomas Freese
 */
public interface ILayoutElement
{
    /**
     *
     */
    Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 11);

    /**
     * ChildElement hinzufügen.
     *
     * @param element {@link ILayoutElement}
     */
    void addElement(ILayoutElement element);

    /**
     * Hintergrundfarbe des Elements.
     *
     * @return {@link Color}
     */
    Color getBackground();

    /**
     * Liefert das ChildElement am Index.
     *
     * @param index int
     *
     * @return {@link ILayoutElement}
     */
    ILayoutElement getElementAt(int index);

    /**
     * Anzahl der ChildElemente.
     *
     * @return int
     */
    int getElementCount();

    /**
     * Font des Elements.
     *
     * @return {@link Font}
     */
    Font getFont();

    /**
     * Vordergrundfarbe des Elements.
     *
     * @return {@link Color}
     */
    Color getForeground();

    /**
     * Höhe des Elements.
     *
     * @return float
     */
    float getHeight();

    /**
     * Insets des Elements.
     *
     * @return {@link Insets}
     */
    Insets getInsets();

    /**
     * Parent des Elements.
     *
     * @return {@link ILayoutElement}
     */
    ILayoutElement getParent();

    /**
     * Breite des Elements.
     *
     * @return float
     */
    float getWidth();

    /**
     * X-Koordinate des Elements. X-Koordinate des Parents wird berücksichtigt.
     *
     * @return float
     */
    float getX();

    /**
     * Y-Koordinate des Elements. Y-Koordinate des Parents wird berücksichtigt.
     *
     * @return float
     */
    float getY();

    /**
     * Malt das Layout in des GraphicContext.
     *
     * @param g2d {@link Graphics2D}
     */
    void paint(Graphics2D g2d);

    /**
     * ChildElement löschen.
     *
     * @param element {@link ILayoutElement}
     *
     * @return true, wenn das Element vorhanden war
     */
    boolean removeElement(ILayoutElement element);

    /**
     * Hintergrundfarbe des Elements.
     *
     * @param color {@link Color}
     */
    void setBackground(Color color);

    /**
     * Font des Elements.
     *
     * @param font {@link Font}
     */
    void setFont(Font font);

    /**
     * Vordergrundfarbe des Elements.
     *
     * @param color {@link Color}
     */
    void setForeground(Color color);

    /**
     * Höhe des Elements.
     *
     * @param height float
     */
    void setHeight(float height);

    /**
     * Insets des Elements.
     *
     * @param insets {@link Insets}
     */
    void setInsets(Insets insets);

    /**
     * Parent des Elements.
     *
     * @param parent {@link ILayoutElement}
     */
    void setParent(ILayoutElement parent);

    /**
     * Breite des Elements.
     *
     * @param width float
     */
    void setWidth(float width);

    /**
     * X-Koordinate des Elements.
     *
     * @param x float
     */
    void setX(float x);

    /**
     * Y-Koordinate des Elements.
     *
     * @param y float
     */
    void setY(float y);
}
