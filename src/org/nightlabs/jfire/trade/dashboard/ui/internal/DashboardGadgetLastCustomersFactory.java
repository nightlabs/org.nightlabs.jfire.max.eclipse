package org.nightlabs.jfire.trade.dashboard.ui.internal;

import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadgetFactory;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetConfigPage;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory;

public class DashboardGadgetLastCustomersFactory extends
	AbstractDashboardGadgetFactory implements IDashboardGadgetFactory {

	@Override
	public IDashboardGadgetConfigPage<?> createConfigurationWizardPage() {
//		return new DashboardGadgetLastCustomersConfigPage();
		return null;
	}

	@Override
	public IDashboardGadget createDashboardGadget() {
		return new DashboardGadgetLastCustomers();
	}
}
