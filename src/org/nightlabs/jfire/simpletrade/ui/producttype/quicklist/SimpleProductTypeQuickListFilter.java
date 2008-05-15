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
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.store.SimpleProductTypeSearchFilter;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilterFactory;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 * @author Fitas Amine <fitas[AT]nightlabs[DOT]de>
 */


public class SimpleProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS_SIMPLE_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME
	};

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

	// TODO temporary workaround - this should come from the query store. 
	private SimpleProductTypeSearchFilter simpleProductTypeSearchFilter;
	private SimpleProductTypeSearchFilter getSimpleProductTypeSearchFilter() {
		if (simpleProductTypeSearchFilter == null)
			simpleProductTypeSearchFilter = new SimpleProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
		
		return simpleProductTypeSearchFilter;
	}

		public void showProductsofVendor(AnchorID vendorID,ProgressMonitor progressMonitor) 
		{
//			getProgressMonitorWrapper().beginTask("Selecting vendor", 100);
//
//			ArticleContainer articleContainer = null;
//
//			if (event.getSubjects().isEmpty())
//				getProgressMonitorWrapper().worked(30);
//			else
//				articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
//						(ArticleContainerID)event.getFirstSubject(),
//						FETCH_GROUPS_ARTICLE_CONTAINER_VENDOR,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						new SubProgressMonitor(getProgressMonitorWrapper(), 30));
//					articleContainer == null ? null : articleContainer.getVendorID());
			
			final SimpleProductTypeSearchFilter searchFilter = getSimpleProductTypeSearchFilter();
			searchFilter.setVendorID(vendorID);
				
			try {
				QueryCollection<SimpleProductTypeSearchFilter> productTypeQueries = new QueryCollection<SimpleProductTypeSearchFilter>(ProductType.class);
				productTypeQueries.add(searchFilter);
				final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(productTypeQueries,FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						progressMonitor);
				
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
	protected void search(org.nightlabs.progress.ProgressMonitor monitor) {
		final SimpleProductTypeSearchFilter searchFilter = getSimpleProductTypeSearchFilter();
		try {
			QueryCollection<SimpleProductTypeSearchFilter> productTypeQueries = new QueryCollection<SimpleProductTypeSearchFilter>(ProductType.class);
			productTypeQueries.add(searchFilter);
			final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(productTypeQueries,
					FETCH_GROUPS_SIMPLE_PRODUCT_TYPE, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					monitor);
			
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
	public Set<Class<? extends Object>> getClasses() {
		Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
		classes.add(SimpleProductType.class);
		return classes;
	}
}
