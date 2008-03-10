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
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.store.SimpleProductTypeSearchFilter;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilterFactory;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SimpleProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] DEFAULT_FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME};

	private SimpleProductTypeTable resultTable;
	
	/**
	 * Factory for extending org.nightlabs.jfire.trade.ui.producttype.quicklist.productTypeQuickListFilterFactory
	 */
	public static class Factory extends AbstractProductTypeQuickListFilterFactory {
		public IProductTypeQuickListFilter createProductTypeQuickListFilter() {
			return new SimpleProductTypeQuickListFilter();
		}
	}

	public SimpleProductTypeQuickListFilter() {
		super();
	}
	
	protected Control doCreateResultViewerControl(Composite parent) {
		resultTable = new SimpleProductTypeTable(parent);
		return resultTable;
	}

	public Control getResultViewerControl() {
		return resultTable;
	}

	public String getDisplayName() {
		return Messages.getString("org.nightlabs.jfire.simpletrade.ui.producttype.quicklist.SimpleProductTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	@Override
	protected void search(ProgressMonitor monitor) {
		final SimpleProductTypeSearchFilter searchFilter = new SimpleProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
		try {
			StoreManager storeManager = StoreManagerUtil.getHome(
					Login.getLogin().getInitialContextProperties()).create();
			final Collection productTypes = storeManager.searchProductTypes(
					searchFilter, DEFAULT_FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					resultTable.setInput(productTypes);
				}
			});
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	@Override
	public Set<Class> getClasses() {
		Set<Class> classes = new HashSet<Class>();
		classes.add(SimpleProductType.class);
		return classes;
	}
}
