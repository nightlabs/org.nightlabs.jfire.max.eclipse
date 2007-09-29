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

package org.nightlabs.jfire.trade.admin.tariff;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffTableCellModifier
	implements ICellModifier
{
	protected TariffListComposite tariffListComposite;
	
	public TariffTableCellModifier(TariffListComposite tariffListComposite)
	{
		this.tariffListComposite = tariffListComposite;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property)
	{
		return TariffListComposite.COLUMN_NAME.equals(property);
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property)
	{
		TariffCarrier tariffCarrier = (TariffCarrier)element;

		if (TariffListComposite.COLUMN_NAME.equals(property)) {
			String txt = tariffCarrier.getTariff().getName().getText(tariffListComposite.getLanguageID()); 
			return txt == null ? "" : txt; //$NON-NLS-1$
		}

		throw new IllegalStateException("property \""+property+"\" unknown!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value)
	{
		TableItem tableItem = (TableItem)element;
		TariffCarrier tariffCarrier = (TariffCarrier)tableItem.getData();
		int columnIdx = tariffListComposite.getColumnIndex(property);

		if (TariffListComposite.COLUMN_NAME.equals(property)) {
			String txt = (String)value;
			tariffCarrier.getTariff().getName().setText(tariffListComposite.getLanguageID(), txt);
			tableItem.setText(columnIdx, txt);
		}
		else
			throw new IllegalStateException("property \""+property+"\" unknown!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
