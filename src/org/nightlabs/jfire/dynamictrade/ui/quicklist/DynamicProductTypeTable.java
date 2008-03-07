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

package org.nightlabs.jfire.dynamictrade.ui.quicklist;

import java.util.Locale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeTable;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class DynamicProductTypeTable 
extends AbstractProductTypeTable<DynamicProductType>
{
	private static class LabelProvider 
	extends org.eclipse.jface.viewers.LabelProvider 
	implements ITableLabelProvider 
	{
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ProductType pt = (DynamicProductType)element;
			StringBuffer sb = new StringBuffer();
			while (pt.getExtendedProductType() != null) {
				sb.insert(0, pt.getName().getText(Locale.getDefault().getLanguage()));
				pt = pt.getExtendedProductType();
				if (pt.getExtendedProductType() != null)
					sb.insert(0, " / "); //$NON-NLS-1$
			}

			return sb.toString();
		}
	}

//	public DynamicProductTypeTable(Composite parent) {
//		super(parent);
//	}
//	
//	public DynamicProductTypeTable(Composite parent, int viewerStyle) {
//		super(parent, viewerStyle);
//	}

	public DynamicProductTypeTable(Composite parent, AbstractProductTypeQuickListFilter filter) {
		super(parent, filter);
	}
		
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
}

