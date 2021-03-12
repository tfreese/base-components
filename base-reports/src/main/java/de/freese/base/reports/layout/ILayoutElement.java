/**
 * 15.04.2008
 */
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
    public static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 11);

    /**
     * ChildElement hinzufuegen.
     * 
     * @param element {@link ILayoutElement}
     */
    public void addElement(ILayoutElement element);

    /**
     * Hintergrundfarbe des Elements.
     * 
     * @return {@link Color}
     */
    public Color getBackground();

    /**
     * Liefert das ChildElement am Index.
     * 
     * @param index int
     * @return {@link ILayoutElement}
     */
    public ILayoutElement getElementAt(int index);

    /**
     * Anzahl der ChildElemente.
     * 
     * @return int
     */
    public int getElementCount();

    /**
     * Font des Elements.
     * 
     * @return {@link Font}
     */
    public Font getFont();

    /**
     * Vordergrundfarbe des Elements.
     * 
     * @return {@link Color}
     */
    public Color getForeground();

    /**
     * Hoehe des Elements.
     * 
     * @return float
     */
    public float getHeight();

    /**
     * Insets des Elements.
     * 
     * @return {@link Insets}
     */
    public Insets getInsets();

    /**
     * Parent des Elements.
     * 
     * @return {@link ILayoutElement}
     */
    public ILayoutElement getParent();

    /**
     * Breite des Elements.
     * 
     * @return float
     */
    public float getWidth();

    /**
     * X-Koordinate des Elements. X-Koordinate des Parents wird beruecktsichtigt.
     * 
     * @return float
     */
    public float getX();

    /**
     * Y-Koordinate des Elements. Y-Koordinate des Parents wird beruecktsichtigt.
     * 
     * @return float
     */
    public float getY();

    /**
     * Malt das Layout in des GraphicContext.
     * 
     * @param g2d {@link Graphics2D}
     */
    public void paint(Graphics2D g2d);

    /**
     * ChildElement loeschen.
     * 
     * @param element {@link ILayoutElement}
     * @return true, wenn das Element vorhanden war
     */
    public boolean removeElement(ILayoutElement element);

    /**
     * Hintergrundfarbe des Elements.
     * 
     * @param color {@link Color}
     */
    public void setBackground(Color color);

    /**
     * Font des Elements.
     * 
     * @param font {@link Font}
     */
    public void setFont(Font font);

    /**
     * Vordergrundfarbe des Elements.
     * 
     * @param color {@link Color}
     */
    public void setForeground(Color color);

    /**
     * Hoehe des Elements.
     * 
     * @param height float
     */
    public void setHeight(float height);

    /**
     * Insets des Elements.
     * 
     * @param insets {@link Insets}
     */
    public void setInsets(Insets insets);

    /**
     * Parent des Elements.
     * 
     * @param parent {@link ILayoutElement}
     */
    public void setParent(ILayoutElement parent);

    /**
     * Breite des Elements.
     * 
     * @param width float
     */
    public void setWidth(float width);

    /**
     * X-Koordinate des Elements.
     * 
     * @param x float
     */
    public void setX(float x);

    /**
     * Y-Koordinate des Elements.
     * 
     * @param y float
     */
    public void setY(float y);
}
