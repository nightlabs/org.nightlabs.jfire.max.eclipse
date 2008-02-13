package org.nightlabs.jfire.trade.admin.ui.deliveryqueue;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.store.deliver.DeliveryQueueConfigModule;

public class DeliveryQueueCfModPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	private IDirtyStateManager dirtyStateManager = new IDirtyStateManager() {
		private boolean dirty;
		
		public boolean isDirty() {
			return this.dirty;
		}

		public void markDirty() {
			this.dirty = true;
			setConfigChanged(true);
		}

		public void markUndirty() {
			this.dirty = false;
			setConfigChanged(false);
		}
	};
	
	private DeliveryQueueConfigurationComposite pqConfigComposite;
	
	
	@Override
	protected void createPreferencePage(Composite parent) {
		pqConfigComposite = new DeliveryQueueConfigurationComposite(parent, dirtyStateManager);
	}

//	@Override
//	protected void updatePreferencePage(ConfigModule configModule)
//	{
//		DeliveryQueueConfigModule pqcm = (DeliveryQueueConfigModule) configModule;
//		pqConfigComposite.loadData(pqcm);
//	}
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
}
