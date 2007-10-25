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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PriceConfigTableLayout extends TableLayout
{
	public static int MIN_COL_WIDTH = 80;
	
	@Override
	public void layout(Composite c, boolean flush) 
	{
		Table table = (Table)c;
		int columnCount = table.getColumnCount();
		int width = table.getBounds().width;
		ScrollBar sb = table.getVerticalBar();
		if(sb.isEnabled() && sb.isVisible()) 
			width -= sb.getSize().x;

		for(int i = 0; i < columnCount; i++) 
    {
      TableColumn tc = table.getColumn(i);
      tc.setWidth(Math.max(MIN_COL_WIDTH, width / columnCount));
    }
  }
}
