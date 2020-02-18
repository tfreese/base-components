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
	/**
     * 
     */
	private String author = "";

	/**
     * 
     */
	private String creator = "";

	/**
     * 
     */
	private String keywords = "";

	/**
	 * Default: Top 20, Left 40, Bottom 20, Right 20.
	 */
	private Insets margins = new Insets(20, 40, 20, 20);

	/**
	 * 
	 */
	private Rectangle pageSize = PageSize.A4;

	/**
     * 
     */
	private String subject = "";

	/**
     * 
     */
	private String title = "";

	/**
	 * Erstellt ein neues {@link DocumentMetaData} Object.
	 */
	public DocumentMetaData()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getAuthor()
	{
		return this.author;
	}

	/**
	 * @return String
	 */
	public String getCreator()
	{
		return this.creator;
	}

	/**
	 * @return String
	 */
	public String getKeywords()
	{
		return this.keywords;
	}

	/**
	 * Default: Top 20, Left 40, Bottom 20, Right 20.
	 * 
	 * @return {@link Insets}
	 */
	public Insets getMargins()
	{
		return this.margins;
	}

	/**
	 * Default: A4 Portrait.
	 * 
	 * @return {@link Rectangle}
	 * @see PageSize
	 */
	public Rectangle getPageSize()
	{
		return this.pageSize;
	}

	/**
	 * @return String
	 */
	public String getSubject()
	{
		return this.subject;
	}

	/**
	 * @return String
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @param author String
	 */
	public void setAuthor(final String author)
	{
		this.author = author;
	}

	/**
	 * @param creator String
	 */
	public void setCreator(final String creator)
	{
		this.creator = creator;
	}

	/**
	 * @param keywords String
	 */
	public void setKeywords(final String keywords)
	{
		this.keywords = keywords;
	}

	/**
	 * Default: Top 20, Left 40, Bottom 20, Right 20.
	 * 
	 * @param margins {@link Insets}
	 */
	public void setMargins(final Insets margins)
	{
		this.margins = margins;
	}

	/**
	 * Default: A4 Portrait.
	 * 
	 * @param pageSize {@link Rectangle}
	 * @see PageSize
	 */
	public void setPageSize(final Rectangle pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * @param subject String
	 */
	public void setSubject(final String subject)
	{
		this.subject = subject;
	}

	/**
	 * @param title String
	 */
	public void setTitle(final String title)
	{
		this.title = title;
	}
}
