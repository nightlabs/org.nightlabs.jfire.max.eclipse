package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers;

import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadgetFactory;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetConfigPage;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory;

/**
 * Factory for "Last Customers" dashboard gadget that is registered as extension to the extension point
 * org.nightlabs.jfire.base.dashboard.ui.dashboardGadgetFactory.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetLastCustomersFactory extends
	AbstractDashboardGadgetFactory implements IDashboardGadgetFactory {

	@Override
	public IDashboardGadgetConfigPage<?> createConfigurationWizardPage() {
		return new DashboardGadgetLastCustomersConfigPage();
	}

	@Override
	public IDashboardGadget createDashboardGadget() {
		return new DashboardGadgetLastCustomers();
	}
}
