package org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.AssignInnerPriceConfigCommand;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.dao.FormulaPriceConfigDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.CellReferenceProductTypeSelector;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class PriceConfigComposite
extends org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite
{
	public PriceConfigComposite(Composite parent) {
		super(parent);
	}

	public PriceConfigComposite(Composite parent, IDirtyStateManager dirtyStateManager) {
		super(parent, dirtyStateManager);
	}

	@SuppressWarnings("unchecked")
	@Implement
	@Override
	protected <P extends GridPriceConfig> Collection<P> storePriceConfigs(Collection<P> priceConfigs, AssignInnerPriceConfigCommand assignInnerPriceConfigCommand)
	{
		try {
			SimpleTradeManager stm = JFireEjbUtil.getBean(SimpleTradeManager.class, Login.getLogin().getInitialContextProperties());
			return stm.storePriceConfigs(priceConfigs, true, assignInnerPriceConfigCommand);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Implement
	@Override
	public AbstractChooseGridPriceConfigWizard createChoosePriceConfigWizard(ProductTypeID parentProductTypeID) {
		return new ChooseSimpleTradePriceConfigWizard(parentProductTypeID);
	}

	@Implement
	@Override
	protected IInnerPriceConfig retrieveInnerPriceConfigForEditing(PriceConfigID priceConfigID)
	{
		return FormulaPriceConfigDAO.sharedInstance().getFormulaPriceConfig(
				priceConfigID,
				FETCH_GROUPS_INNER_PRICE_CONFIG_FOR_EDITING,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
	}

	@Override
	public CellReferenceProductTypeSelector createCellReferenceProductTypeSelector() {
		return new SimpleProductTypeSelector();
	}
}
