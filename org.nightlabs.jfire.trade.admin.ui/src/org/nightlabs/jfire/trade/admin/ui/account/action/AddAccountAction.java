package org.nightlabs.jfire.trade.admin.ui.account.action;

import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.jfire.trade.admin.ui.account.CreateAccountWizard;

public class AddAccountAction
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled() {
		return true;
	}

	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		CreateAccountWizard.open();
	}
}
