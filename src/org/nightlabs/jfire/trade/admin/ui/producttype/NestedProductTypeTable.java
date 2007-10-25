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

package org.nightlabs.jfire.trade.admin.ui.producttype;

import java.util.Locale;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractInvertableTableSorter;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableSortSelectionListener;
import org.nightlabs.jfire.store.NestedProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class NestedProductTypeTable
		extends AbstractTableComposite<NestedProductType>
		implements ISelectionProvider
{
	protected static class NestedProductTypeContentProvider
	implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return productType.getNestedProductTypes().toArray();
		}

		public void dispose()
		{
		}

		private ProductType productType;

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput == null)
				return;

			if (!(newInput instanceof ProductType))
				throw new IllegalArgumentException("input must be null or an instance of ProductType, but is " + (newInput == null ? "null" : newInput.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$

			this.productType = (ProductType) newInput;
		}
	}

	protected static class NestedProductTypeLabelProvider
	extends LabelProvider
	implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (! (element instanceof NestedProductType))
				return "Invalid element! Must be NestedProductType, but is " + (element == null ? "null" : element.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$

			NestedProductType nestedProductType = (NestedProductType) element;
//			if (! (nestedProductType.getInnerProductType() instanceof SimpleProductType))
//				return "nestedProductType.innerProductType is an instance of " + (nestedProductType.getInnerProductType() == null ? "null" : nestedProductType.getInnerProductType().getClass().getName()) + ", but must be a SimpleProductType!";

			ProductType productType = nestedProductType.getInnerProductType();

			int ci = 0;
			if (ci == columnIndex)
				return productType.getName().getText(Locale.getDefault().getLanguage());

			if (++ci == columnIndex)
				return String.valueOf(nestedProductType.getQuantity());

			return ""; //$NON-NLS-1$
		}
	}

//	protected static abstract class NestedProductTypeViewerSorter
//	extends AbstractInvertableTableSorter
//	{
//		public int compare(Viewer viewer, Object e1, Object e2)
//		{
//			NestedProductType npt1 = (NestedProductType) e1;
//			NestedProductType npt2 = (NestedProductType) e2;
//			return _compare(viewer, npt1, npt2);
//		}
//
//		protected abstract int _compare(Viewer viewer, NestedProductType npt1, NestedProductType npt2);
//	}

	protected static class NestedProductTypeViewerSorter_Name
	extends AbstractInvertableTableSorter<NestedProductType>
	{
		@Override
		protected int _compare(Viewer viewer, NestedProductType npt1, NestedProductType npt2)
		{
			return getCollator().compare(
					npt1.getInnerProductType().getName().getText(Locale.getDefault().getLanguage()),
					npt2.getInnerProductType().getName().getText(Locale.getDefault().getLanguage()));
		}
	}

	protected static class NestedProductTypeViewerSorter_Quantity
	extends AbstractInvertableTableSorter<NestedProductType>
	{
		@Override
		protected int _compare(Viewer viewer, NestedProductType npt1, NestedProductType npt2)
		{
			int qty1 = npt1.getQuantity();
			int qty2 = npt2.getQuantity();

			if (qty1 == qty2)
				return 0;

			return qty1 < qty2 ? -1 : 1;
		}
	}

	public NestedProductTypeTable(Composite parent)
	{
		super(parent, SWT.NONE, true);		
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.NestedProductTypeTable.nameTableColumn.text")); //$NON-NLS-1$
		TableSortSelectionListener tsslName = new TableSortSelectionListener(tableViewer, col, new NestedProductTypeViewerSorter_Name(), SWT.UP);

		col = new TableColumn(table, SWT.RIGHT);
		col.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.NestedProductTypeTable.quantityTableColumn.text")); //$NON-NLS-1$
		new TableSortSelectionListener(tableViewer, col, new NestedProductTypeViewerSorter_Quantity(), SWT.UP);

		table.setLayout(new WeightedTableLayout(new int[]{90, 10}));

		tsslName.chooseColumnForSorting();
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new NestedProductTypeContentProvider());
		tableViewer.setLabelProvider(new NestedProductTypeLabelProvider());
	}
}
