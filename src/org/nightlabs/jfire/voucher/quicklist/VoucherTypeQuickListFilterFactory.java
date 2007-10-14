package org.nightlabs.jfire.voucher.quicklist;

import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilterFactory;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter;

public class VoucherTypeQuickListFilterFactory
extends AbstractProductTypeQuickListFilterFactory
{

	public IProductTypeQuickListFilter createProductTypeQuickListFilter() {
		return new VoucherTypeQuickListFilter();
	}
}
