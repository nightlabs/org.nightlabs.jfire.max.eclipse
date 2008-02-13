package org.nightlabs.jfire.voucher.admin.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractSaleAccessControlSection;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeDetailPage;
import org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlHelper;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSaleAccessControlSection
extends AbstractSaleAccessControlSection
{

	public VoucherTypeSaleAccessControlSection(IProductTypeDetailPage page, Composite parent) {
		super(page, parent);
	}

	@Override
	protected SaleAccessControlHelper createSaleAccessControlHelper() {
		return new VoucherTypeSaleAccessControlHelper();
	}

}
