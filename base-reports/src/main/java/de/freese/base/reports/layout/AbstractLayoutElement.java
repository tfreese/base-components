// Created: 15.04.2008
package de.freese.base.reports.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Basisimplementierung eines LayoutElementes.
 *
 * @author Thomas Freese
 */
public abstract class AbstractLayoutElement implements LayoutElement
{
    /**
     *
     */
    private final List<LayoutElement> elements = new ArrayList<>(20);
    /**
     *
     */
    private Color background = Color.WHITE;
    /**
     *
     */
    private Font font = DEFAULT_FONT;
    /**
     *
     */
    private Color foreground = Color.BLACK;
    /**
     *
     */
    private float height;
    /**
     *
     */
    private Insets insets = new Insets(0, 0, 0, 0);
    /**
     *
     */
    private String name = "";
    /**
     *
     */
    private LayoutElement parent;
    /**
     *
     */
    private float width;
    /**
     *
     */
    private float x;
    /**
     *
     */
    private float y;

    /**
     * Creates a new AbstractLayoutElement object.
     */
    protected AbstractLayoutElement()
    {
        super();
    }

    /**
     * Creates a new AbstractLayoutElement object.
     *
     * @param name String
     */
    protected AbstractLayoutElement(final String name)
    {
        super();

        this.name = name;
    }

    /**
     * @see LayoutElement#addElement(LayoutElement)
     */
    @Override
    public void addElement(final LayoutElement element)
    {
        if (!this.elements.contains(element))
        {
            this.elements.add(element);

            element.setParent(this);
        }
    }

    /**
     * Erzeugt ein Image des Layouts.
     *
     * @return Image
     */
    public BufferedImage createImage()
    {
        BufferedImage bufferedImage = new BufferedImage((int) getWidth() + 1, (int) getHeight() + 1, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        paint(g2d);

        g2d.dispose();

        return bufferedImage;
    }

    /**
     * @see LayoutElement#getBackground()
     */
    @Override
    public Color getBackground()
    {
        return this.background;
    }

    /**
     * @see LayoutElement#getElementAt(int)
     */
    @Override
    public LayoutElement getElementAt(final int index)
    {
        return this.elements.get(index);
    }

    /**
     * @see LayoutElement#getElementCount()
     */
    @Override
    public int getElementCount()
    {
        return this.elements.size();
    }

    /**
     * @see LayoutElement#getFont()
     */
    @Override
    public Font getFont()
    {
        return this.font;
    }

    /**
     * @see LayoutElement#getForeground()
     */
    @Override
    public Color getForeground()
    {
        return this.foreground;
    }

    /**
     * @see LayoutElement#getHeight()
     */
    @Override
    public float getHeight()
    {
        return this.height;
    }

    /**
     * @see LayoutElement#getInsets()
     */
    @Override
    public Insets getInsets()
    {
        return this.insets;
    }

    /**
     * @see LayoutElement#getParent()
     */
    @Override
    public LayoutElement getParent()
    {
        return this.parent;
    }

    /**
     * @see LayoutElement#getWidth()
     */
    @Override
    public float getWidth()
    {
        return this.width;
    }

    /**
     * @see LayoutElement#getX()
     */
    @Override
    public float getX()
    {
        return (getParent() == null) ? this.x : (this.x + getParent().getX());
    }

    /**
     * @see LayoutElement#getY()
     */
    @Override
    public float getY()
    {
        return (getParent() == null) ? this.y : (this.y + getParent().getY());
    }

    /**
     * @see LayoutElement#paint(java.awt.Graphics2D)
     */
    @Override
    public void paint(final Graphics2D g2d)
    {
        g2d.setColor(getBackground());
        g2d.fillRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

        paintName(g2d);
        paintChilds(g2d);

        // Rahmen des Elements malen
        g2d.setColor(Color.BLUE);
        g2d.drawRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }

    /**
     * @see LayoutElement#removeElement(LayoutElement)
     */
    @Override
    public boolean removeElement(final LayoutElement element)
    {
        boolean contains = this.elements.remove(element);

        if (contains)
        {
            element.setParent(null);
        }

        return contains;
    }

    /**
     * Speichert das Image als JPG oder PNG.
     *
     * @param bufferedImage BufferedImage
     * @param fileName String
     * @param type jpg oder png
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void saveImageAsJPEG(final BufferedImage bufferedImage, final String fileName, final String type) throws Exception
    {
        File file = new File(fileName);
        ImageIO.write(bufferedImage, type, file);
    }

    /**
     * @see LayoutElement#setBackground(java.awt.Color)
     */
    @Override
    public void setBackground(final Color color)
    {
        this.background = color;
    }

    /**
     * @see LayoutElement#setFont(java.awt.Font)
     */
    @Override
    public void setFont(final Font font)
    {
        this.font = font;
    }

    /**
     * @see LayoutElement#setForeground(java.awt.Color)
     */
    @Override
    public void setForeground(final Color color)
    {
        this.foreground = color;
    }

    /**
     * @see LayoutElement#setHeight(float)
     */
    @Override
    public void setHeight(final float height)
    {
        this.height = height;
    }

    /**
     * @see LayoutElement#setInsets(java.awt.Insets)
     */
    @Override
    public void setInsets(final Insets insets)
    {
        this.insets = insets;
    }

    /**
     * @see LayoutElement#setParent(LayoutElement)
     */
    @Override
    public void setParent(final LayoutElement parent)
    {
        this.parent = parent;
    }

    /**
     * @see LayoutElement#setWidth(float)
     */
    @Override
    public void setWidth(final float width)
    {
        this.width = width;
    }

    /**
     * @see LayoutElement#setX(float)
     */
    @Override
    public void setX(final float x)
    {
        this.x = x;
    }

    /**
     * @see LayoutElement#setY(float)
     */
    @Override
    public void setY(final float y)
    {
        this.y = y;
    }

    /**
     * Name des Elements.
     *
     * @return String
     */
    protected String getName()
    {
        return this.name;
    }

    /**
     * Malt die ChildElemente.
     *
     * @param g2d Graphics2D
     */
    protected void paintChilds(final Graphics2D g2d)
    {
        for (int i = 0; i < getElementCount(); i++)
        {
            LayoutElement element = getElementAt(i);

            element.paint(g2d);
        }
    }

    /**
     * Malt den Namen des Elements.
     *
     * @param g2d Graphics2D
     */
    protected void paintName(final Graphics2D g2d)
    {
        g2d.setColor(getForeground());

        String text = getName() + " (" + getWidth() + "x" + getHeight() + ")";
        g2d.drawString(text, getX() + 3, (getY() + getHeight()) - 3);
    }

    /**
     * Name des Elements.
     *
     * @param name String
     */
    protected void setName(final String name)
    {
        this.name = name;
    }
}
