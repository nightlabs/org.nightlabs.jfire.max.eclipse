package org.nightlabs.jfire.trade.dashboard.ui.internal.clientScripts;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;

public class DashboardGadgetClientScriptsConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	public DashboardGadgetClientScriptsConfigPage() {
		super(DashboardGadgetClientScriptsConfigPage.class.getName());
	}

	@Override
	public Control createPageContents(Composite parent) {
		return parent;
	}

	@Override
	public void configure(DashboardGadgetLayoutEntry<?> layoutEntry) {
	}
	
}
