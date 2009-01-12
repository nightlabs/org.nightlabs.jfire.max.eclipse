/**
 * 
 */
package org.nightlabs.jfire.trade.ui.modeofdelivery.config;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.store.deliver.config.ModeOfDeliveryConfigModule;

/**
 * Workstation preference page for {@link ModeOfDeliveryConfigModule}.
 * It delegates to {@link ModeOfDeliveryConfigModuleComposite}.
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfDeliveryWorkstationCfModPreferencePage extends AbstractWorkstationConfigModulePreferencePage {

	private ModeOfDeliveryConfigModuleComposite configModuleComposite;

	public ModeOfDeliveryWorkstationCfModPreferencePage() {
	}

	public ModeOfDeliveryWorkstationCfModPreferencePage(String title) {
		super(title);
	}

	public ModeOfDeliveryWorkstationCfModPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createConfigModuleController()
	 */
	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new ModeOfDeliveryConfigModuleController(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) {
		configModuleComposite = new ModeOfDeliveryConfigModuleComposite(parent, SWT.NONE, getPageDirtyStateManager());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updateConfigModule()
	 */
	@Override
	public void updateConfigModule() {
		ModeOfDeliveryConfigModule configModule = (ModeOfDeliveryConfigModule) getConfigModuleController().getConfigModule();
		configModuleComposite.updateConfigModule(configModule);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updatePreferencePage()
	 */
	@Override
	protected void updatePreferencePage() {
		ModeOfDeliveryConfigModule configModule = (ModeOfDeliveryConfigModule) getConfigModuleController().getConfigModule();
		configModuleComposite.updateComposite(configModule);
	}

}
