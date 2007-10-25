package org.nightlabs.jfire.voucher.admin.ui.editor;

import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.producttype.AbstractSaleAccessControlHelper;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSaleAccessControlHelper 
extends AbstractSaleAccessControlHelper 
{
//	public Set<String> getFetchGroupsProductType()
//	{
//		Set<String> fetchGroups = super.getFetchGroupsProductType();
//		fetchGroups.add(VoucherType.FETCH_GROUP_VOUCHER_LAYOUT);
//		return fetchGroups;
//	}

	@Override
	public void setProductType(ProductType productType)
	{
		super.setProductType(productType);
	}

	public VoucherType getVoucherType()
	{
		return (VoucherType) getProductType();
	}

	@Override
	public boolean canConfirm(boolean silent)
	{
		if (!super.canConfirm(silent))
			return false;

// It can be confirmed and saleable, because it is possible to use a different DeliveryProcessor (no need to print)
// and the printing delivery-processor checks for the existence of a voucher-layout.

//		if (getVoucherType().getVoucherLayout() == null) {
//			if (!silent) {
//				MessageDialog.openError(
//						RCPUtil.getActiveWorkbenchShell(),
//						"No VoucherLayout assigned",
//						"Voucher cannot be confirmed when no VoucherLayout is assigned.");
//			}
//
//			return false;
//		}

		return true;
	}

	@Override
	public boolean canSetSaleable(boolean silent, boolean saleable)
	{
		if (!super.canSetSaleable(silent, saleable))
			return false;

//		if (getVoucherType().getVoucherLayout() == null) {
//			if (!silent) {
//				MessageDialog.openError(
//						RCPUtil.getActiveWorkbenchShell(),
//						"No VoucherLayout assigned",
//						"Voucher cannot be confirmed when no VoucherLayout is assigned.");
//			}
//			return false;
//		}

		return true;
	}
}
