package org.nightlabs.jfire.trade.admin.ui.overview;

public class UnitCategoryFactory 
extends AbstractTradeAdminCategoryFactory
{
	@Override
	public TradeAdminCategory createTradeAdminCategory() {
		return new UnitCategory(this);
	}
}
