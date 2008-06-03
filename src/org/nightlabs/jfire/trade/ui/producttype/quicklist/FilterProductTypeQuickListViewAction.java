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

package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import java.util.SortedSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.base.ui.overview.search.QueryFilterDialog;
import org.nightlabs.jfire.base.ui.overview.search.QueryFilterFactory;
import org.nightlabs.jfire.base.ui.overview.search.QueryFilterFactoryRegistry;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class FilterProductTypeQuickListViewAction 
implements IViewActionDelegate 
{
	private ProductTypeQuickListView view;
	
	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = (ProductTypeQuickListView)view;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IProductTypeQuickListFilter selectedFilter = view.getSelectedFilter();
		if (selectedFilter != null) {
			QueryFilterDialog dialog = new QueryFilterDialog(view.getSite().getShell(),
					getScope(), selectedFilter.getQueryResultClass());
			int returnCode = dialog.open();
			if (returnCode == Window.OK) {
				
			}			
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		IProductTypeQuickListFilter selectedFilter = view.getSelectedFilter();
		if (selectedFilter != null) {		
			SortedSet<QueryFilterFactory> factories = QueryFilterFactoryRegistry.sharedInstance().getQueryFilterCompositesFor(
					getScope(), selectedFilter.getQueryResultClass());
			action.setEnabled(factories != null);
			return;
		}
		action.setEnabled(false);
	}
	
	protected String getScope() {
		return "global";
	}
}
