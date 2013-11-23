package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadgetFactory;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetConfigPage;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory;

/**
 * @author sschefczyk
 *
 */
public class DashboardGadgetInvoiceFactory extends AbstractDashboardGadgetFactory implements IDashboardGadgetFactory {

	public DashboardGadgetInvoiceFactory() {}

	@Override
	public IDashboardGadgetConfigPage<?> createConfigurationWizardPage() {
		return new ConfigureInvoiceGadgetPage();
	}

	@Override
	public IDashboardGadget createDashboardGadget() {
		return new DashboardGadgetInvoice();
	}

}
