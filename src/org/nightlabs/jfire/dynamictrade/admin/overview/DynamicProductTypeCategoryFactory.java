package org.nightlabs.jfire.dynamictrade.admin.overview;

import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategoryFactory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategory;

public class DynamicProductTypeCategoryFactory
		extends AbstractTradeAdminCategoryFactory
{
	public TradeAdminCategory createTradeAdminCategory()
	{
		return new DynamicProductTypeCategory(this);
	}
}
