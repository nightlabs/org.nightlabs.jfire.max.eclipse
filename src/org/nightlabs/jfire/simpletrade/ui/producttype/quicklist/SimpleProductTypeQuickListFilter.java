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
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.store.search.SimpleProductTypeQuery;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.search.VendorDependentQuery;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilterFactory;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.QuickListFilterQueryResultKey;
import org.nightlabs.progress.SubProgressMonitor;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Fitas Amine <fitas[AT]nightlabs[DOT]de>
 * @author Daniel Mazurek <daniel[AT]nightlabs[DOT]de>
 */
public class SimpleProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS_SIMPLE_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME
	};

	/**
	 * Factory for extending org.nightlabs.jfire.trade.ui.producttype.quicklist.productTypeQuickListFilterFactory
	 */
	public static class Factory extends AbstractProductTypeQuickListFilterFactory {
		public IProductTypeQuickListFilter createProductTypeQuickListFilter() {
			return new SimpleProductTypeQuickListFilter();
		}
	}

	private SimpleProductTypeTable resultTable;

	@Override
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

	@SuppressWarnings("unchecked")
	@Override
	protected void search(org.nightlabs.progress.ProgressMonitor monitor)
	{
		monitor.beginTask("Searching Simple ProductTypes", 100);
		final QueryCollection<VendorDependentQuery> productTypeQueries = getQueryCollection(new SubProgressMonitor(monitor, 50));
		try {
			QuickListFilterQueryResultKey cacheKey = createQueryResultCacheKey(new SubProgressMonitor(monitor, 10));
			final Collection[] productTypes = new Collection[1];
			productTypes[0] = (Collection<ProductType>) Cache.sharedInstance().get(
					null, cacheKey, 
					FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			if (productTypes[0] == null) {

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						resultTable.setLoadingMessage("Searching Simple ProductTypes");
					}
				});
				productTypes[0] = ProductTypeDAO.sharedInstance().getProductTypes(
						productTypeQueries,
						FETCH_GROUPS_SIMPLE_PRODUCT_TYPE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50));
				Cache.sharedInstance().put(
						null, cacheKey, productTypes[0], 
						FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			}
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (resultTable.isDisposed())
						return;
					resultTable.setInput(productTypes[0]);
				}
			});
			monitor.done();
		} catch (Exception x) {
			monitor.setCanceled(true);
			throw new RuntimeException(x);
		}
	}

	@Override
	public Set<Class<? extends Object>> getClasses() {
		Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
		classes.add(SimpleProductType.class);
		return classes;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter#getQueryClass()
	 */
	@Override
	protected Class<? extends VendorDependentQuery> getQueryClass() {
		return SimpleProductTypeQuery.class;
	}

}
