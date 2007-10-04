package org.nightlabs.jfire.simpletrade.admin.overview;

import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategoryFactory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class SimpleProductTypeCategoryFactory 
extends AbstractTradeAdminCategoryFactory 
{
	public TradeAdminCategory createTradeAdminCategory()
	{
		return new SimpleProductTypeCategory(this);
	}
}
