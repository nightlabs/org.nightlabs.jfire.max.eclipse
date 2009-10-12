package org.nightlabs.jfire.trade.admin.ui.overview;

/**
 *
 * @author vince - vince at guinaree dot com
 *
 */
public class CurrencyTypeCategoryFactory
extends AbstractTradeAdminCategoryFactory
{
	@Override
	public TradeAdminCategory createTradeAdminCategory()
	{
		return new CurrencyTypeCategory(this);
	}

}
