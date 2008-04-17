package org.nightlabs.jfire.voucher.ui.quicklist;

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
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.store.VoucherTypeSearchFilter;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.notification.NotificationAdapterWorkerThreadAsync;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class VoucherTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] DEFAULT_FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME};


	public static String[] FETCH_GROUPS_VENDOR = new String[] {
		FetchPlan.DEFAULT,
		ArticleContainer.FETCH_GROUP_VENDOR_ID,
	};


	public VoucherTypeQuickListFilter() {
		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				ArticleContainer.class, notificationListenerVendorSelected);
	}

	private NotificationListener notificationListenerVendorSelected = new NotificationAdapterWorkerThreadAsync() {
		public void notify(NotificationEvent event) {

			ArticleContainer ac = null;

			if (!event.getSubjects().isEmpty())			
			{			
				ac = ArticleContainerDAO.sharedInstance().getArticleContainer((ArticleContainerID)event.getFirstSubject(),FETCH_GROUPS_VENDOR, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,new NullProgressMonitor());

			}

			if(ac == null)	
				return;

			final VoucherTypeSearchFilter searchFilter = new VoucherTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
			searchFilter.setVendorID(ac.getVendorID());

			try {
				StoreManager storeManager = StoreManagerUtil.getHome(
						Login.getLogin().getInitialContextProperties()).create();
				final Collection<ProductType> voucherTypes = storeManager.searchProductTypes(
						searchFilter, DEFAULT_FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						voucherTypeTable.setInput(voucherTypes);
					}
				});
			} catch (Exception x) {
				throw new RuntimeException(x);
			}



		}
	};


	private VoucherTypeTable voucherTypeTable;

	@Override
	protected Control doCreateResultViewerControl(Composite parent)
	{
		voucherTypeTable = new VoucherTypeTable(parent);
		return voucherTypeTable;
	}

	public Control getResultViewerControl()
	{
		return voucherTypeTable;
	}

	public String getDisplayName()
	{
		return Messages.getString("org.nightlabs.jfire.voucher.ui.quicklist.VoucherTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	@Override
	protected void search(ProgressMonitor monitor) {
		final VoucherTypeSearchFilter searchFilter = new VoucherTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
		try {
			StoreManager storeManager = StoreManagerUtil.getHome(
					Login.getLogin().getInitialContextProperties()).create();
			final Collection<ProductType> voucherTypes = storeManager.searchProductTypes(
					searchFilter, DEFAULT_FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					voucherTypeTable.setInput(voucherTypes);
				}
			});
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public Set<Class<? extends Object>> getClasses() {
		Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
		classes.add(VoucherType.class);
		return classes;
	}

}
