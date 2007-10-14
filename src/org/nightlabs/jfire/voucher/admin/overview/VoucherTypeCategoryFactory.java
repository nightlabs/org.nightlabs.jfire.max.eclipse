package org.nightlabs.jfire.voucher.admin.overview;

import org.nightlabs.jfire.trade.admin.ui.overview.AbstractTradeAdminCategoryFactory;
import org.nightlabs.jfire.trade.admin.ui.overview.TradeAdminCategory;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class VoucherTypeCategoryFactory 
extends AbstractTradeAdminCategoryFactory 
{
	public TradeAdminCategory createTradeAdminCategory()
	{
		return new VoucherTypeCategory(this);
	}
}
