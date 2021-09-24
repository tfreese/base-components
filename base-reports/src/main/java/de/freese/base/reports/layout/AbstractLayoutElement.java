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
public abstract class AbstractLayoutElement implements ILayoutElement
{
    /**
     *
     */
    private Color background = Color.WHITE;
    /**
     *
     */
    private final List<ILayoutElement> elements = new ArrayList<>(20);
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
    private ILayoutElement parent;
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
     * @see de.freese.base.reports.layout.ILayoutElement#addElement(de.freese.base.reports.layout.ILayoutElement)
     */
    @Override
    public void addElement(final ILayoutElement element)
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
     * @see de.freese.base.reports.layout.ILayoutElement#getBackground()
     */
    @Override
    public Color getBackground()
    {
        return this.background;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getElementAt(int)
     */
    @Override
    public ILayoutElement getElementAt(final int index)
    {
        return this.elements.get(index);
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getElementCount()
     */
    @Override
    public int getElementCount()
    {
        return this.elements.size();
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getFont()
     */
    @Override
    public Font getFont()
    {
        return this.font;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getForeground()
     */
    @Override
    public Color getForeground()
    {
        return this.foreground;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getHeight()
     */
    @Override
    public float getHeight()
    {
        return this.height;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getInsets()
     */
    @Override
    public Insets getInsets()
    {
        return this.insets;
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
     * @see de.freese.base.reports.layout.ILayoutElement#getParent()
     */
    @Override
    public ILayoutElement getParent()
    {
        return this.parent;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getWidth()
     */
    @Override
    public float getWidth()
    {
        return this.width;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getX()
     */
    @Override
    public float getX()
    {
        return (getParent() == null) ? this.x : (this.x + getParent().getX());
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#getY()
     */
    @Override
    public float getY()
    {
        return (getParent() == null) ? this.y : (this.y + getParent().getY());
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#paint(java.awt.Graphics2D)
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
     * Malt die ChildElemente.
     *
     * @param g2d Graphics2D
     */
    protected void paintChilds(final Graphics2D g2d)
    {
        for (int i = 0; i < getElementCount(); i++)
        {
            ILayoutElement element = getElementAt(i);

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
     * @see de.freese.base.reports.layout.ILayoutElement#removeElement(de.freese.base.reports.layout.ILayoutElement)
     */
    @Override
    public boolean removeElement(final ILayoutElement element)
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
     * @throws Exception Falls was schief geht.
     */
    public void saveImageAsJPEG(final BufferedImage bufferedImage, final String fileName, final String type) throws Exception
    {
        File file = new File(fileName);
        ImageIO.write(bufferedImage, type, file);
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setBackground(java.awt.Color)
     */
    @Override
    public void setBackground(final Color color)
    {
        this.background = color;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setFont(java.awt.Font)
     */
    @Override
    public void setFont(final Font font)
    {
        this.font = font;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setForeground(java.awt.Color)
     */
    @Override
    public void setForeground(final Color color)
    {
        this.foreground = color;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setHeight(float)
     */
    @Override
    public void setHeight(final float height)
    {
        this.height = height;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setInsets(java.awt.Insets)
     */
    @Override
    public void setInsets(final Insets insets)
    {
        this.insets = insets;
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

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setParent(de.freese.base.reports.layout.ILayoutElement)
     */
    @Override
    public void setParent(final ILayoutElement parent)
    {
        this.parent = parent;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setWidth(float)
     */
    @Override
    public void setWidth(final float width)
    {
        this.width = width;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setX(float)
     */
    @Override
    public void setX(final float x)
    {
        this.x = x;
    }

    /**
     * @see de.freese.base.reports.layout.ILayoutElement#setY(float)
     */
    @Override
    public void setY(final float y)
    {
        this.y = y;
    }
}
