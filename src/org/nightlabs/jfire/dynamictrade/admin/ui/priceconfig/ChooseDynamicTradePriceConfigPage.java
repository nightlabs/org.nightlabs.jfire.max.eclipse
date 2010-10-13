package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.List;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.dao.DynamicTradePriceConfigDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ChooseDynamicTradePriceConfigPage
extends AbstractChooseGridPriceConfigPage
{
	public ChooseDynamicTradePriceConfigPage(final ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	@Override
	protected List<? extends IInnerPriceConfig> retrievePriceConfigs(final ProgressMonitor monitor)
	{
		return DynamicTradePriceConfigDAO.sharedInstance().getDynamicTradePriceConfigs(
				AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

	}

	@Override
	public IInnerPriceConfig createPriceConfig(final I18nText priceConfigName) {
		final DynamicTradePriceConfig pc = new DynamicTradePriceConfig(null);
		pc.getName().copyFrom(priceConfigName);
		return pc;
	}

}
