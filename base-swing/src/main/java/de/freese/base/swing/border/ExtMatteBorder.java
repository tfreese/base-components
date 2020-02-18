package de.freese.base.swing.border;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.border.MatteBorder;

/**
 * Border mit definierbaren Dicken der einzelnen Linien.
 * 
 * @author Thomas Freese
 */
public class ExtMatteBorder extends MatteBorder
{
	/**
	 *
	 */
	private static final long serialVersionUID = 823952493901941086L;

	/**
	 * Erstellt ein neues {@link ExtMatteBorder} Object.
	 * 
	 * @param tileIcon {@link Icon}
	 */
	public ExtMatteBorder(final Icon tileIcon)
	{
		super(tileIcon);
	}

	/**
	 * Erstellt ein neues {@link ExtMatteBorder} Object.
	 * 
	 * @param borderInsets {@link Insets}
	 * @param matteColor {@link Color}
	 */
	public ExtMatteBorder(final Insets borderInsets, final Color matteColor)
	{
		super(borderInsets, matteColor);
	}

	/**
	 * Erstellt ein neues {@link ExtMatteBorder} Object.
	 * 
	 * @param borderInsets {@link Insets}
	 * @param tileIcon {@link Icon}
	 */
	public ExtMatteBorder(final Insets borderInsets, final Icon tileIcon)
	{
		super(borderInsets, tileIcon);
	}

	/**
	 * Erstellt ein neues {@link ExtMatteBorder} Object.
	 * 
	 * @param top int
	 * @param left int
	 * @param bottom int
	 * @param right int
	 * @param matteColor {@link Color}
	 */
	public ExtMatteBorder(final int top, final int left, final int bottom, final int right,
			final Color matteColor)
	{
		super(top, left, bottom, right, matteColor);
	}

	/**
	 * Erstellt ein neues {@link ExtMatteBorder} Object.
	 * 
	 * @param top int
	 * @param left int
	 * @param bottom int
	 * @param right int
	 * @param tileIcon {@link Icon}
	 */
	public ExtMatteBorder(final int top, final int left, final int bottom, final int right,
			final Icon tileIcon)
	{
		super(top, left, bottom, right, tileIcon);
	}

	/**
	 * @param bottom int
	 */
	public void setBottom(final int bottom)
	{
		this.bottom = bottom;
	}

	/**
	 * @param left int
	 */
	public void setLeft(final int left)
	{
		this.left = left;
	}

	/**
	 * @param matteColor {@link Color}
	 */
	public void setMatteColor(final Color matteColor)
	{
		this.color = matteColor;
	}

	/**
	 * @param right int
	 */
	public void setRight(final int right)
	{
		this.right = right;
	}

	/**
	 * @param tileIcon {@link Icon}
	 */
	public void setTileIcon(final Icon tileIcon)
	{
		this.tileIcon = tileIcon;
	}

	/**
	 * @param top int
	 */
	public void setTop(final int top)
	{
		this.top = top;
	}
}
