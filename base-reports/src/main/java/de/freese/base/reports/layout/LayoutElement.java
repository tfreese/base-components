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
public interface LayoutElement
{
    Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 11);

    void addElement(LayoutElement element);

    Color getBackground();

    LayoutElement getElementAt(int index);

    int getElementCount();

    Font getFont();

    Color getForeground();

    double getHeight();

    Insets getInsets();

    LayoutElement getParent();

    double getWidth();

    /**
     * X-Koordinate des Elements.<br/>
     * X-Koordinate des Parents wird berücksichtigt.
     */
    double getX();

    /**
     * Y-Koordinate des Elements.<br/>
     * Y-Koordinate des Parents wird berücksichtigt.
     */
    double getY();

    void paint(Graphics2D g2d);

    boolean removeElement(LayoutElement element);

    void setBackground(Color color);

    void setFont(Font font);

    void setForeground(Color color);

    void setHeight(double height);

    void setInsets(Insets insets);

    void setParent(LayoutElement parent);

    void setWidth(double width);

    void setX(double x);

    void setY(double y);
}
