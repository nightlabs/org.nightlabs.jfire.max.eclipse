package org.nightlabs.jfire.trade.ui.tariff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffOrderConfigModule;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;

public class TariffOrderCfModPrefPage extends AbstractUserConfigModulePreferencePage {

	public TariffOrderCfModPrefPage() {
		super(TariffOrderCfModPrefPage.class.getName());
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new AbstractConfigModuleController(this) {
			public Class<? extends ConfigModule> getConfigModuleClass() {
				return TariffOrderConfigModule.class;
			}

			private Set<String> fetchGroups = null;

			public Set<String> getConfigModuleFetchGroups() {
				if (fetchGroups == null) {
					fetchGroups = new HashSet<String>(getCommonConfigModuleFetchGroups());
					fetchGroups.add(TariffOrderConfigModule.FETCH_GROUP_TARIFF_ORDER_CONFIG_MODULE);
					fetchGroups.add(FetchPlan.DEFAULT);
				}

				return fetchGroups;
			}
		};
	}

	private TariffOrderCfModComposite tariffOrderCfModComposite;

	@Override
	protected void createPreferencePage(Composite parent) {
		tariffOrderCfModComposite = new TariffOrderCfModComposite(parent, SWT.NONE, getPageDirtyStateManager());
	}

	@Override
	public void updateConfigModule() {
		Map<Tariff, Integer> tariffOrderMap = new HashMap<Tariff, Integer>();
		int index = 0;
		for (Tariff tariff : tariffOrderCfModComposite.getOrderedTariffs()) {
			tariffOrderMap.put(tariff, index++);
		}

		getConfigModule().setTariffOrderMap(tariffOrderMap);
	}

	private TariffOrderConfigModule getConfigModule() {
		return (TariffOrderConfigModule) getConfigModuleController().getConfigModule();
	}

	@Override
	protected void updatePreferencePage() {
		tariffOrderCfModComposite.loadTariffs(getConfigModule());
	}

	@Override
	protected void setEditable(boolean editable)
	{
		tariffOrderCfModComposite.setEditable(editable);
	}
}
