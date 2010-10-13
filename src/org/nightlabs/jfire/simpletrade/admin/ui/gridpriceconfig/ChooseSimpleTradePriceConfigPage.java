package org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig;

import java.util.List;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.simpletrade.dao.FormulaPriceConfigDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ChooseSimpleTradePriceConfigPage
extends AbstractChooseGridPriceConfigPage
{
	public ChooseSimpleTradePriceConfigPage(final ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	@Override
	protected List<? extends IInnerPriceConfig> retrievePriceConfigs(final ProgressMonitor monitor)
	{
		return FormulaPriceConfigDAO.sharedInstance().getFormulaPriceConfigs(
				AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	public IInnerPriceConfig createPriceConfig(final I18nText priceConfigName) {
		final FormulaPriceConfig priceConfig = new FormulaPriceConfig(null);
		priceConfig.getName().copyFrom(priceConfigName);
		return priceConfig;
	}

}
