package org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ChooseSimpleTradePriceConfigWizard 
extends AbstractChooseGridPriceConfigWizard 
{
	/**
	 * @param parentProductTypeID
	 */
	public ChooseSimpleTradePriceConfigWizard(ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard#createChooseGridPriceConfigPage(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	@Implement
	protected AbstractChooseGridPriceConfigPage createChooseGridPriceConfigPage(
			ProductTypeID parentProductTypeID) 
	{
		return new ChooseSimpleTradePriceConfigPage(parentProductTypeID);
	}

}
