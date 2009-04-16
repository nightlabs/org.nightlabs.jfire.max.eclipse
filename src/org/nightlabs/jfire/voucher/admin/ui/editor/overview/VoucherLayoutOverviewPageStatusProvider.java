package org.nightlabs.jfire.voucher.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class VoucherLayoutOverviewPageStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, VoucherType.FETCH_GROUP_VOUCHER_LAYOUT};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#createStatus(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	protected IStatus createStatus(ProductType productType) 
	{
		if (productType != null && productType instanceof VoucherType) {
			VoucherType voucherType = (VoucherType) productType;
			VoucherLayout voucherLayout = voucherType.getVoucherLayout();
			StringBuilder sb = new StringBuilder();
			sb.append("VoucherLayout: ");
			String name = "NONE";
			int severity = IStatus.ERROR;
			if (voucherLayout != null) {
				name = voucherLayout.getFileName();
				severity = IStatus.OK;
			}
			sb.append(name);
			return new Status(severity, getStatusPluginId(), sb.toString());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#getFetchGroups()
	 */
	@Override
	protected String[] getFetchGroups() {
		return FETCH_GROUPS;
	}

}
