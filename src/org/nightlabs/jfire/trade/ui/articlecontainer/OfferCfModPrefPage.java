package org.nightlabs.jfire.trade.ui.articlecontainer;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.OfferConfigModule;

public class OfferCfModPrefPage
extends AbstractWorkstationConfigModulePreferencePage
{

	public OfferCfModPrefPage() {
		super(OfferCfModPrefPage.class.getName());
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new AbstractConfigModuleController(this) {

			@Override
			public Class<? extends ConfigModule> getConfigModuleClass() {
				return org.nightlabs.jfire.trade.config.OfferConfigModule.class;
			}

			@Override
			public Set<String> getConfigModuleFetchGroups() {
				return getCommonConfigModuleFetchGroups();
			}
		};
	}

	private OfferCfModPrefPageComposite offerCfModPrefPageComposite;

	@Override
	protected void createPreferencePage(Composite parent) {
		offerCfModPrefPageComposite = new OfferCfModPrefPageComposite(parent, SWT.NONE, getPageDirtyStateManager());
	}

	@Override
	public void updateConfigModule() {
		if (!offerCfModPrefPageComposite.isDisposed()) {
			offerCfModPrefPageComposite.updateConfigModule(
					(OfferConfigModule) getConfigModuleController().getConfigModule()
			);
		}
	}

	@Override
	protected void updatePreferencePage() {
		if (!offerCfModPrefPageComposite.isDisposed()) {
			offerCfModPrefPageComposite.updatePreferencePage(
					(OfferConfigModule) getConfigModuleController().getConfigModule()
			);
		}
	}

}
