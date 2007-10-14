package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class ChooseDynamicTradePriceConfigWizard 
extends AbstractChooseGridPriceConfigWizard 
{
	/**
	 * @param parentProductTypeID
	 */
	public ChooseDynamicTradePriceConfigWizard(ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard#createChooseGridPriceConfigPage(org.nightlabs.jfire.store.ProductType)
	 */
	@Implement
	protected AbstractChooseGridPriceConfigPage createChooseGridPriceConfigPage(
			ProductTypeID parentProductTypeID) 
	{
		return new ChooseDynamicTradePriceConfigPage(parentProductTypeID);
	}

}
