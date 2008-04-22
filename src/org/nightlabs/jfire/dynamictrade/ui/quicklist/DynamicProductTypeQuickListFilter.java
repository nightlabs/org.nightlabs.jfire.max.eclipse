package org.nightlabs.jfire.dynamictrade.ui.quicklist;

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
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductTypeSearchFilter;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class DynamicProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
		ProductType.FETCH_GROUP_NAME};

	private DynamicProductTypeTable dynamicProductTypeTable =null;

	public DynamicProductTypeQuickListFilter() {
		super();

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				ArticleContainer.class, notificationListenerArticleContainerSelected);
	}

	@Override
	public Control doCreateResultViewerControl(Composite parent)
	{
		dynamicProductTypeTable = new DynamicProductTypeTable(parent);
		return dynamicProductTypeTable;
	}


	
	// TODO temporary workaround - this should come from the query store. 
	private DynamicProductTypeSearchFilter dynamicProductTypeSearchFilter;
	private DynamicProductTypeSearchFilter getDynamicProductTypeSearchFilter() {
		if (dynamicProductTypeSearchFilter == null)
			dynamicProductTypeSearchFilter = new DynamicProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
		
		return dynamicProductTypeSearchFilter;
	}


	
	private NotificationListener notificationListenerArticleContainerSelected = new NotificationAdapterJob("Selecting vendor") {
		public void notify(NotificationEvent event) {

			ArticleContainer articleContainer = null;

			
			if (event.getSubjects().isEmpty())
				getProgressMonitorWrapper().worked(30);
			else
				articleContainer = ArticleContainerDAO.sharedInstance().getArticleContainer(
						(ArticleContainerID)event.getFirstSubject(),
						FETCH_GROUPS_ARTICLE_CONTAINER_VENDOR,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(getProgressMonitorWrapper(), 30));
			
			final DynamicProductTypeSearchFilter searchFilter = getDynamicProductTypeSearchFilter();
			searchFilter.setVendorID(articleContainer == null ? null : articleContainer.getVendorID());
			try {
		
				QueryCollection<DynamicProductTypeSearchFilter> productTypeQueries = new QueryCollection<DynamicProductTypeSearchFilter>(ProductType.class);
				productTypeQueries.add(searchFilter);
				
				final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(productTypeQueries,FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new SubProgressMonitor(getProgressMonitorWrapper(), 70));
				
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						dynamicProductTypeTable.setInput(productTypes);
					}
				});
			} catch (Exception x) {
				throw new RuntimeException(x);
			}



		}
	};


	public String getDisplayName()
	{
		return Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	@Override
	public Control getResultViewerControl() {
		return dynamicProductTypeTable;
	}

	@Override
	public Set<Class<? extends Object>> getClasses() {
		Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
		classes.add(DynamicProductType.class);
		return classes;
	}

	@Override
	protected void search(ProgressMonitor monitor) {
		
		final DynamicProductTypeSearchFilter searchFilter = getDynamicProductTypeSearchFilter();
		try {
			QueryCollection<DynamicProductTypeSearchFilter> productTypeQueries = new QueryCollection<DynamicProductTypeSearchFilter>(ProductType.class);
			productTypeQueries.add(searchFilter);
			final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(productTypeQueries,
					FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor);
			
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dynamicProductTypeTable.setInput(productTypes);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
