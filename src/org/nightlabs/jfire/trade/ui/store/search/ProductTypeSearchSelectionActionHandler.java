package org.nightlabs.jfire.trade.ui.store.search;

import org.nightlabs.jfire.base.ui.search.AbstractJDOSelectionZoneActionHandler;
import org.nightlabs.jfire.trade.ui.TradePlugin;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ProductTypeSearchSelectionActionHandler
//extends AbstractSelectionZoneActionHandler
extends AbstractJDOSelectionZoneActionHandler
{
	public ProductTypeSearchSelectionActionHandler() {
	}

	@Override
	public String getSelectionZone() {
		return TradePlugin.ZONE_SALE;
	}

}
