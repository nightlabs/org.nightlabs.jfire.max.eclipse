package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;

public class DeliveryQueueCfModPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	private DeliveryQueueConfigurationComposite pqConfigComposite;

	@Override
	protected void createPreferencePage(Composite parent) {
		pqConfigComposite = new DeliveryQueueConfigurationComposite(parent, getPageDirtyStateManager());
	}

	@Override
	protected void updatePreferencePage()
	{
		DeliveryQueueConfigModule pqcm = (DeliveryQueueConfigModule) getConfigModuleController().getConfigModule();
		pqConfigComposite.loadData(pqcm);
	}

	@Override
	public void updateConfigModule()
	{
		DeliveryQueueConfigModule pqcm = (DeliveryQueueConfigModule) getConfigModuleController().getConfigModule();
		pqConfigComposite.storeChanges(pqcm);
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new DeliveryQueueConfigModuleController(this);
	}

	@Override
	protected void setBodyContentEditable(boolean editable)
	{
		pqConfigComposite.setReadOnly(! editable);
	}
}
