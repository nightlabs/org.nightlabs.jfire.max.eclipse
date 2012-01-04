package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal;

import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadgetFactory;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetConfigPage;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory;

public class DashboardGadgetClientScriptsFactory extends
	AbstractDashboardGadgetFactory implements IDashboardGadgetFactory {

	@Override
	public IDashboardGadgetConfigPage<?> createConfigurationWizardPage() {
		return new DashboardGadgetClientScriptsConfigPage();
	}

	@Override
	public IDashboardGadget createDashboardGadget() {
		return new DashboardGadgetClientScripts();
	}

}
