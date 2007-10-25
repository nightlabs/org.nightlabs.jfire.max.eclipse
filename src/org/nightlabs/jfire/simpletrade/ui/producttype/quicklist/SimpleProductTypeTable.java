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

package org.nightlabs.jfire.simpletrade.ui.producttype.quicklist;

import java.util.Collection;
import java.util.Locale;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SimpleProductTypeTable extends AbstractTableComposite<SimpleProductType> {

	private static class ContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection)inputElement).toArray();
			}
			else
				throw new IllegalArgumentException("SimpleProductTypeTable.ContentProvider expects a Collection as inputElement. Recieved "+inputElement.getClass().getName()); //$NON-NLS-1$
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private static class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return ((SimpleProductType)element).getName().getText(Locale.getDefault().getLanguage());
		}
	}

	private SimpleProductTypeQuickListFilter filter;

	public SimpleProductTypeTable(Composite parent, SimpleProductTypeQuickListFilter filter, int style) {
		super(parent, SWT.NONE, true, style);
		this.filter = filter;
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (!(event.getSelection() instanceof IStructuredSelection))
					throw new ClassCastException("selection is an instance of "+(event.getSelection()==null?"null":event.getSelection().getClass().getName())+" instead of "+IStructuredSelection.class.getName()+"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				Object elem = getFirstSelectedElement();
				ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(elem);
				SimpleProductTypeTable.this.filter.setSelectedProductTypeID(productTypeID);
			}
		});
	}
	
	public SimpleProductTypeTable(Composite parent, SimpleProductTypeQuickListFilter filter) {
		this(parent, filter, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
	}

	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.simpletrade.ui.producttype.quicklist.SimpleProductTypeTable.nameTableColumn.text")); //$NON-NLS-1$
//		TableLayout l = new TableLayout();
//		l.addColumnData(new ColumnWeightData(1));
//		table.setLayout(l);
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}

	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
}

