package org.nightlabs.jfire.dynamictrade.ui.quicklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductTypeSearchFilter;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.notification.NotificationAdapterWorkerThreadAsync;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class DynamicProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
		ProductType.FETCH_GROUP_NAME};

	private DynamicProductTypeTable dynamicProductTypeTable =null;

	public DynamicProductTypeQuickListFilter() {
		super();

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				ArticleContainer.class, notificationListenerVendorSelected);
	}

	@Override
	public Control doCreateResultViewerControl(Composite parent)
	{
		dynamicProductTypeTable = new DynamicProductTypeTable(parent);
		return dynamicProductTypeTable;
	}


	private NotificationListener notificationListenerVendorSelected = new NotificationAdapterWorkerThreadAsync() {
		public void notify(NotificationEvent event) {

			ArticleContainer ac = null;

			if (!event.getSubjects().isEmpty())			
			{			
				ac = ArticleContainerDAO.sharedInstance().getArticleContainer((ArticleContainerID)event.getFirstSubject(),FETCH_GROUPS_VENDOR, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,new NullProgressMonitor());

			}

			if(ac == null || dynamicProductTypeTable.isDisposed() || dynamicProductTypeTable==null)	
				return;



			final DynamicProductTypeSearchFilter searchFilter = new DynamicProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
			searchFilter.setVendorID(ac.getVendorID());
			try {
				StoreManager storeManager = StoreManagerUtil.getHome(
						Login.getLogin().getInitialContextProperties()).create();
				final Collection<ProductType> productTypes = storeManager.searchProductTypes(
						searchFilter, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

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
		try {
			final Collection<DynamicProductType> dynamicProductTypes =
				DynamicProductTypeDAO.sharedInstance().getDynamicProductTypes(
						ProductType.INHERITANCE_NATURE_LEAF, Boolean.TRUE, FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dynamicProductTypeTable.setInput(dynamicProductTypes);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
