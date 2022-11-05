package de.freese.base.reports.exporter.pdf;

import java.awt.Insets;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

/**
 * Definiert MetaDaten eines PDF Dokumentes.
 *
 * @author Thomas Freese
 */
public class DocumentMetaData
{
    private String author = "";

    private String creator = "";

    private String keywords = "";
    /**
     * Default: Top 20, Left 40, Bottom 20, Right 20.
     */
    private Insets margins = new Insets(20, 40, 20, 20);

    private Rectangle pageSize = PageSize.A4;

    private String subject = "";

    private String title = "";

    public String getAuthor()
    {
        return this.author;
    }

    public String getCreator()
    {
        return this.creator;
    }

    public String getKeywords()
    {
        return this.keywords;
    }

    /**
     * Default: Top 20, Left 40, Bottom 20, Right 20.
     */
    public Insets getMargins()
    {
        return this.margins;
    }

    /**
     * Default: A4 Portrait.
     *
     * @see PageSize
     */
    public Rectangle getPageSize()
    {
        return this.pageSize;
    }

    public String getSubject()
    {
        return this.subject;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setAuthor(final String author)
    {
        this.author = author;
    }

    public void setCreator(final String creator)
    {
        this.creator = creator;
    }

    public void setKeywords(final String keywords)
    {
        this.keywords = keywords;
    }

    /**
     * Default: Top 20, Left 40, Bottom 20, Right 20.
     */
    public void setMargins(final Insets margins)
    {
        this.margins = margins;
    }

    /**
     * Default: A4 Portrait.
     *
     * @see PageSize
     */
    public void setPageSize(final Rectangle pageSize)
    {
        this.pageSize = pageSize;
    }

    public void setSubject(final String subject)
    {
        this.subject = subject;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }
}
