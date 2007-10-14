package org.nightlabs.jfire.voucher.quicklist;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.voucher.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherTypeSearchFilter;
import org.nightlabs.progress.ProgressMonitor;

public class VoucherTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
//extends AbstractProductTypeViewerQuickListFilter
{

	public static String[] DEFAULT_FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME};

	private VoucherTypeTable voucherTypeTable;

	public Control createResultViewerControl(Composite parent)
	{
		voucherTypeTable = new VoucherTypeTable(parent, this, AbstractTableComposite.DEFAULT_STYLE_SINGLE);
		return voucherTypeTable;
	}
	
	public Control getResultViewerControl()
	{
		return voucherTypeTable;
	}
	
//	@Override
//	public StructuredViewer createViewer(Composite parent) {
//		voucherTypeTable = new VoucherTypeTable(parent, this);
//		return voucherTypeTable.getTableViewer();
//	}
	
	public String getDisplayName()
	{
		return Messages.getString("org.nightlabs.jfire.voucher.quicklist.VoucherTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	public void search(ProgressMonitor monitor) { 
		final VoucherTypeSearchFilter searchFilter = new VoucherTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);

		new Job(Messages.getString("org.nightlabs.jfire.voucher.quicklist.VoucherTypeQuickListFilter.loadVoucherTypesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					final Collection voucherTypes = storeManager.searchProductTypes(searchFilter, DEFAULT_FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {			
							voucherTypeTable.setInput(voucherTypes);
						}
					});
					return Status.OK_STATUS;
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		}.schedule();
	}

}
