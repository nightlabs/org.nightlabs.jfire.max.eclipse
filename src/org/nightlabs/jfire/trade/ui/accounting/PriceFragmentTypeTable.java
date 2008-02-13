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

package org.nightlabs.jfire.trade.ui.accounting;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PriceFragmentTypeTable extends AbstractTableComposite {
	
	private static final String[] DEFAULT_FETCH_GROUPS = new String[]{
		FetchPlan.DEFAULT,
		PriceFragmentType.FETCH_GROUP_NAME
	};
	
	private static class ContentProvider extends TableContentProvider {
		/**
		 * @see org.nightlabs.base.ui.table.TableContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return PriceFragmentTypeProvider.sharedInstance().getPriceFragmentTypes(DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT).toArray(); // TODO load asynchronously in a Job!!! And not in the ContentProvider!
		}
	}

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof PriceFragmentType) {
				return ((PriceFragmentType)element).getName().getText(Locale.getDefault().getLanguage());
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * Assumes to be added to a GridLayout.
	 * 
	 * @param parent
	 * @param style
	 */
	public PriceFragmentTypeTable(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Returns the (first) selected PriceFragmentType or null.
	 * @return The (first) selected PriceFragmentType or null.
	 */
	public PriceFragmentType getSelectedPriceFragmentType() {
		if (getTable().getSelectionCount() == 1) {
			return (PriceFragmentType)getTable().getSelection()[0].getData();
		}
		return null;
	}

	/**
	 * Returns all selected PriceFragmentType in a Set.
	 * @return All selected PriceFragmentType in a Set.
	 */
	public Set getSelectedPriceFragmentTypes() {
		Set result = new HashSet();
		TableItem[] items = getTable().getSelection();
		for (int i = 0; i < items.length; i++) {
			result.add(items[i].getData());
		}
		return result;
	}
	
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
//		table.setHeaderVisible(true); // This UI element is only used in contexts (wizard-page + tab-item) where there is already written "Price fragment type" above => no need for this header!
		table.setHeaderVisible(false); // true is default => forcing false
		(new TableColumn(table, SWT.LEFT)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.PriceFragmentTypeTable.priceFragmentTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[]{1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setInput(tableViewer.getContentProvider());
	}

}
