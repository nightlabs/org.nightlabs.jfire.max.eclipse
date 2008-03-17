package org.nightlabs.jfire.voucher.ui.quicklist;

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
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.store.VoucherTypeSearchFilter;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class VoucherTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] DEFAULT_FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME};

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
