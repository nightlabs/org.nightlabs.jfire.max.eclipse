/**
 *
 */
package org.nightlabs.jfire.trade.ui.modeofpayment.config;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.accounting.pay.config.ModeOfPaymentConfigModule;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;

/**
 * Workstation preference page for {@link ModeOfPaymentConfigModule}.
 * It delegates to {@link ModeOfPaymentConfigModuleComposite}.
 *
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class ModeOfPaymentWorkstationCfModPreferencePage extends AbstractWorkstationConfigModulePreferencePage {

	private ModeOfPaymentConfigModuleComposite configModuleComposite;

	public ModeOfPaymentWorkstationCfModPreferencePage() {
	}

	public ModeOfPaymentWorkstationCfModPreferencePage(String title) {
		super(title);
	}

	public ModeOfPaymentWorkstationCfModPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createConfigModuleController()
	 */
	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new ModeOfPaymentConfigModuleController(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) {
		configModuleComposite = new ModeOfPaymentConfigModuleComposite(parent, SWT.NONE, getPageDirtyStateManager());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updateConfigModule()
	 */
	@Override
	public void updateConfigModule() {
		ModeOfPaymentConfigModule configModule = (ModeOfPaymentConfigModule) getConfigModuleController().getConfigModule();
		if (!configModuleComposite.isDisposed()) {
			configModuleComposite.updateConfigModule(configModule);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updatePreferencePage()
	 */
	@Override
	protected void updatePreferencePage() {
		ModeOfPaymentConfigModule configModule = (ModeOfPaymentConfigModule) getConfigModuleController().getConfigModule();
		if (!configModuleComposite.isDisposed()) {
			configModuleComposite.updateComposite(configModule);
		}
	}

}
