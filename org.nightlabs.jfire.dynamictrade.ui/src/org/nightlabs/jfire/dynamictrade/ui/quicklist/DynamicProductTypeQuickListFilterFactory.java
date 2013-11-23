package org.nightlabs.jfire.dynamictrade.ui.quicklist;

import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilterFactory;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilter;

public class DynamicProductTypeQuickListFilterFactory
extends AbstractProductTypeQuickListFilterFactory
{
	public IProductTypeQuickListFilter createProductTypeQuickListFilter() {
		return new DynamicProductTypeQuickListFilter();
	}
}
