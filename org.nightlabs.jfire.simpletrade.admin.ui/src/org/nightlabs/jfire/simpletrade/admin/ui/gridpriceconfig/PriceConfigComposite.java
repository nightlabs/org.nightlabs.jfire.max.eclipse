package org.nightlabs.jfire.simpletrade.admin.ui.gridpriceconfig;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.AssignInnerPriceConfigCommand;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerRemote;
import org.nightlabs.jfire.simpletrade.dao.FormulaPriceConfigDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.CellReferenceProductTypeSelector;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

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

	@Override
	protected <P extends GridPriceConfig> Collection<P> storePriceConfigs(Collection<P> priceConfigs, AssignInnerPriceConfigCommand assignInnerPriceConfigCommand)
	{
		try {
			SimpleTradeManagerRemote stm = JFireEjb3Factory.getRemoteBean(SimpleTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			Collection<GridPriceConfig> gpcs = CollectionUtil.castCollection(priceConfigs);
			gpcs = stm.storePriceConfigs(gpcs, true, assignInnerPriceConfigCommand);
			return CollectionUtil.castCollection(gpcs);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public AbstractChooseGridPriceConfigWizard createChoosePriceConfigWizard(ProductTypeID parentProductTypeID) {
		return new ChooseSimpleTradePriceConfigWizard(parentProductTypeID);
	}

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
