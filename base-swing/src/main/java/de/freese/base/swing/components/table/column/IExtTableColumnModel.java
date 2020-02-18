/*
 * $Id: IExtTableColumnModel.java,v 1.1 2009-06-30 17:49:56 tommy Exp $ Copyright 2004 Sun
 * Microsystems, Inc., 4150 Network Circle, Santa Clara, California 95054, U.S.A. All rights
 * reserved. This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
 * Floor, Boston, MA 02110-1301 USA
 */

package de.freese.base.swing.components.table.column;

import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Interface fuer ein erweitertes ColumnModel.
 * 
 * @author Thomas Freese
 */
public interface IExtTableColumnModel extends TableColumnModel
{
	/**
	 * Liefert die Anzahl der Spalten.
	 * 
	 * @param includeHidden boolean
	 * @return int
	 */
	public int getColumnCount(boolean includeHidden);

	/**
	 * Liefert die Spalte an der View-Position.
	 * 
	 * @param columnIndex int
	 * @return {@link ExtTableColumn}
	 */
	public ExtTableColumn getColumnExt(int columnIndex);

	/**
	 * Liefert die Spalte des Identifiers.
	 * 
	 * @param identifier Object
	 * @return ExtTableColumn
	 */
	public ExtTableColumn getColumnExt(Object identifier);

	/**
	 * Liefert die Spalten.
	 * 
	 * @param includeHidden boolean
	 * @return {@link List}
	 */
	public List<TableColumn> getColumns(boolean includeHidden);

}
