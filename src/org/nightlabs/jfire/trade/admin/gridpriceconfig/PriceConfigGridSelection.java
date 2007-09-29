/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.admin.gridpriceconfig;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PriceConfigGridSelection implements ISelection
{
	private Point cursorCellCoordinate;
	private PriceConfigGridCell cursorCell;
	private Collection selectedCellCoordinates;
	private Collection selectedCells;

	public PriceConfigGridSelection(
			Point cursorCellCoordinate, PriceConfigGridCell cursorCell,
			Collection selectedCellCoordinates, Collection selectedCells)
	{
		this.cursorCellCoordinate = cursorCellCoordinate;
		this.cursorCell = cursorCell;
		this.selectedCellCoordinates = selectedCellCoordinates;
		this.selectedCellCoordinates = selectedCells;
	}

	/**
	 * @return Returns instances of <tt>Point</tt> where x and y specify the column and the row
	 *   index of the selected cells. Note, that the cursor coordinate might be missing in
	 *   this <tt>Collection</tt>. Additionally, you should be aware that header cells might be
	 *   selected.
	 *
	 * @see #getDataLeftTop()
	 */
	public Collection getSelectedCellCoordinates()
	{
		return selectedCellCoordinates;
	}

	/**
	 * @return Returns the coordinate of the table's cursor where y is the row and x is the
	 *   column. Note, that the data does usually start at x = 1 and y = 1, because there
	 *   is a horizontal and a vertical header.
	 *
	 * @see #getDataLeftTop()
	 * @see #getSelectedCellCoordinates()
	 */
	public Point getCursorCellCoordinate()
	{
		return cursorCellCoordinate;
	}

	/**
	 * @return Returns the cursorCell.
	 */
	public PriceConfigGridCell getCursorCell()
	{
		return cursorCell;
	}
	/**
	 * @return Returns the selectedCells.
	 */
	public Collection getSelectedCells()
	{
		return selectedCells;
	}
	/**
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return false;
	}

}
