package org.nightlabs.jfire.issuetracking.dashboard.ui.internal;

import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadgetFactory;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetConfigPage;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class IssueDashboardGadgetFactory extends AbstractDashboardGadgetFactory {

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory#createConfigurationWizardPage()
	 */
	@Override
	public IDashboardGadgetConfigPage createConfigurationWizardPage() {
		return new IssueDashboardGadgetConfigPage();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.dashboard.ui.IDashboardGadgetFactory#createDashboardGadget()
	 */
	@Override
	public IDashboardGadget createDashboardGadget() {
		return new IssueDashboardGadget();
	}

}
