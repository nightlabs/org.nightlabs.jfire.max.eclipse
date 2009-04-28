package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.gridpriceconfig.AssignInnerPriceConfigCommand;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculator;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerRemote;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.dao.DynamicTradePriceConfigDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.CellReferenceProductTypeSelector;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValueSelector;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.ProductTypeSelector;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.Util;

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
			// We clear the packaging result price configs for optimisation reasons (prevent transferring to the server).
			// They are cleared in any case during pre-attach and pre-store of DynamicTradePriceConfig.
			Collection<P> clonedPCs = Util.cloneSerializable(priceConfigs);
			Collection<DynamicTradePriceConfig> dynamicTradePriceConfigs = new ArrayList<DynamicTradePriceConfig>(clonedPCs.size());
			for (P priceConfig : clonedPCs) {
				DynamicTradePriceConfig dtpc = (DynamicTradePriceConfig) priceConfig;
				dtpc.clearPackagingResultPriceConfigs();
				dynamicTradePriceConfigs.add(dtpc);
			}
			DynamicTradeManagerRemote dtm = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			dynamicTradePriceConfigs = dtm.storeDynamicTradePriceConfigs(dynamicTradePriceConfigs, true, assignInnerPriceConfigCommand);
			return CollectionUtil.castCollection(dynamicTradePriceConfigs);
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

//	@Override
//	protected Composite createLeftCarrierComposite(Composite parent)
//	{
//		SashForm sf = new SashForm(parent, SWT.VERTICAL);
//		sf.setWeights(new int[] {1, 4});
//		return sf;
//	}

	@Override
	protected ProductTypeSelector createProductTypeSelector(Composite parent)
	{
		return new ProductTypeSelectorHiddenImpl();
	}

	@Override
	protected DimensionValueSelector createDimensionValueSelector(Composite parent)
	{
		//return new DimensionValueSelectorImpl(parent);
		return new DimensionValueSelectorImpl(parent, this);

//		return super.createDimensionValueSelector(parent);
	}

	@Override
	protected IInnerPriceConfig retrieveInnerPriceConfigForEditing(PriceConfigID priceConfigID)
	{
		return DynamicTradePriceConfigDAO.sharedInstance().getDynamicTradePriceConfig(
				priceConfigID,
				FETCH_GROUPS_INNER_PRICE_CONFIG_FOR_EDITING,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());
	}

	@Override
	protected PriceCalculator createPriceCalculator(
			ProductType packageProductType) {
		return new org.nightlabs.jfire.dynamictrade.accounting.priceconfig.PriceCalculator(packageProductType, createCustomerGroupMapper(), createTariffMapper());
	}

	@Override
	public AbstractChooseGridPriceConfigWizard createChoosePriceConfigWizard(ProductTypeID parentProductTypeID)
	{
		return new ChooseDynamicTradePriceConfigWizard(parentProductTypeID);
	}

	@Override
	public void _setPackageProductType(ProductType packageProductType)
	{
		packageProductType.setPackagePriceConfig(null); // this will not be stored to the server - during price-calculation, we need an IResultPriceConfig (which is done by the PriceCalculator, if this field is null)
		super._setPackageProductType(packageProductType);
	}

	@Override
	public boolean submit()
	{
		return super.submit();
	}

	@Override
	public CellReferenceProductTypeSelector createCellReferenceProductTypeSelector() {
		return null; // we do not have nested dynamic product types => no need for this dimension
	}
}
