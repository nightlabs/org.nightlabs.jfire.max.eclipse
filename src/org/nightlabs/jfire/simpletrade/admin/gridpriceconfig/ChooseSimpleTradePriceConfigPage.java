package org.nightlabs.jfire.simpletrade.admin.gridpriceconfig;

import java.util.List;

import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.simpletrade.dao.FormulaPriceConfigDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigPage;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class ChooseSimpleTradePriceConfigPage
extends AbstractChooseGridPriceConfigPage
{
	public ChooseSimpleTradePriceConfigPage(ProductTypeID parentProductTypeID) {
		super(parentProductTypeID);
	}

	@Implement
	protected List<? extends IInnerPriceConfig> retrievePriceConfigs(ProgressMonitor monitor) 
	{
		return FormulaPriceConfigDAO.sharedInstance().getFormulaPriceConfigs(
				AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//		try {
//			SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			List<FormulaPriceConfig> pcs = new ArrayList<FormulaPriceConfig>(stm.getFormulaPriceConfigs(
//					AbstractChooseGridPriceConfigPage.FETCH_GROUPS_PRICE_CONFIG, 
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));
//			return pcs;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}

}
