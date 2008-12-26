package org.nightlabs.jfire.voucher.ui.quicklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.search.VendorDependentQuery;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.store.search.VoucherTypeQuery;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class VoucherTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] DEFAULT_FETCH_VOUCHER_TYPE_GROUP = new String[] {
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
	protected void search(ProgressMonitor monitor)
	{
		monitor.beginTask("Searching VoucherTypes", 100);
		final QueryCollection<VendorDependentQuery> queryCollection = getQueryCollection(new SubProgressMonitor(monitor, 50));
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (voucherTypeTable.isDisposed())
						return;
					voucherTypeTable.setLoadingMessage("Searching VoucherTypes");
				}
			});
			final Collection<ProductType> voucherTypes = ProductTypeDAO.sharedInstance().getProductTypes(
					queryCollection,
					DEFAULT_FETCH_VOUCHER_TYPE_GROUP,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 50));
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (voucherTypeTable.isDisposed())
						return;
					voucherTypeTable.setInput(voucherTypes);
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
		classes.add(VoucherType.class);
		return classes;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter#getQueryClass()
	 */
	@Override
	protected Class<? extends VendorDependentQuery> getQueryClass() {
		return VoucherTypeQuery.class;
	}

}
