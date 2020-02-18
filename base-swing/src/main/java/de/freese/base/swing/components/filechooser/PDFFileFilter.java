package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} fuer CSV-Dateien.
 * 
 * @author Thomas Freese
 */
public class PDFFileFilter extends GenericFileFilter
{
	/**
	 * Erstellt ein neues {@link PDFFileFilter} Object.
	 */
	public PDFFileFilter()
	{
		super(true, ".csv");
	}
}
