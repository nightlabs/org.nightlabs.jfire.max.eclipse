package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.List;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
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
	public ChooseDynamicTradePriceConfigPage(ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	@Override
	@Implement
	protected List<? extends IInnerPriceConfig> retrievePriceConfigs(ProgressMonitor monitor)
	{
		return DynamicTradePriceConfigDAO.sharedInstance().getDynamicTradePriceConfigs(
				AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

//		try {
//			DynamicTradeManager stm = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			List<DynamicTradePriceConfig> pcs = new ArrayList<DynamicTradePriceConfig>(stm.getDynamicTradePriceConfigs(
//					AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
//			return pcs;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}

}
